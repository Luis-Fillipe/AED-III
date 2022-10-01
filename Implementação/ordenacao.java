import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ordenacao {
    RandomAccessFile arq;

    public static void swap(usuario[] array, int i, int menor) {
        usuario change = array[i].clone();
        array[i] = array[menor].clone();
        array[menor] = change.clone();
    }

    static void selecao(usuario[] users, int tamanho) {

        int menor;
        for (int i = 0; i < tamanho - 1; i++) {
            menor = i;
            for (int j = i + 1; j < tamanho; j++) {
                if (users[j].getId() < users[menor].getId()) {
                    menor = j;

                }

            }
            swap(users, i, menor);
        }
    }

    /*
     * balanceada() - Utilizaremos 5 registros e 2 caminhos para realizar a
     * intercalação balanceada
     */
    public void blocos() throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "r");
        RandomAccessFile raf[] = new RandomAccessFile[2];
        String part1 = "bd/temp";
        String part2 = ".db";
        // crio o array de arquivos temporarios
        for (int i = 0; i < 2; i++) {
            raf[i] = new RandomAccessFile(part1 + i + part2, "rw");
        }

        ArrayList<usuario> users = new ArrayList<usuario>();
        arq.seek(4); // pulo o cabeçalho
        int qtd = 0;
        while ((arq.length() - arq.getFilePointer()) > 0) {
            char lapide = (char) arq.readByte();
            if (lapide == ' ') { // se nao for lapide eu pego o registro
                arq.readInt();
                usuario user = new usuario();
                user.setID(arq.readInt());
                user.setNomeUsuario(arq.readUTF());
                user.setNome(arq.readUTF());
                int sizeEmails = arq.readByte();
                for (int i = 0; i < sizeEmails; i++) {
                    user.setEmail(arq.readUTF());
                }
                user.setSenha(arq.readUTF());
                user.setCidade(arq.readUTF());
                String cpf = "";
                for (int i = 0; i < 11; i++) {
                    cpf += (char) arq.readByte();
                }
                user.setCPF(cpf);
                user.setTransferencias(arq.readInt());
                user.setSaldo(arq.readInt());
                users.add(user); // pego esse registro e jogo em um array de registros
                qtd++;
            } else {
                int size = arq.readInt();
                arq.seek(arq.getFilePointer() + size);
            }
        }

        usuario usersArray[] = new usuario[5]; // um novo array de tamanho fixo para ordenar
        int j = 0;
        int controle = 0;
        for (int i = 0; i < users.size(); i++) {
            if (j == 5) { // se ja peguei 5 registros

                j = 0; // 0 meu contador
                selecao(usersArray, usersArray.length); // aplico seleção nos 5 registros que eu tenho
                if (controle < 2) { // controle é o meu indice no array de arquivos
                    raf[controle].seek(raf[controle].length());
                    raf[controle].writeInt(5); // coloco o tamanho do bloco
                    // insiro meus 5 registros no bloco
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], raf[controle]);
                    }
                    controle++;
                } else {
                    // se meu controle for maior que 2
                    controle = 0; // volto pro indice 0 no array de arquivos e repito o processo acima
                    raf[controle].seek(raf[controle].length());
                    raf[controle].writeInt(5);
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], raf[controle]);
                    }
                    controle++;
                }
            }
            usersArray[j] = users.get(i).clone();
            j++;
        }

        if (j <= 5) { // se sobrar algum registro que pode ser feito um bloco menor que 5
            selecao(usersArray, j); // aplico seleção e repito os processos acima
            if (controle < 2) {
                raf[controle].seek(raf[controle].length());
                raf[controle].writeInt(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], raf[controle]);
                }
                controle++;
            } else {
                controle = 0;
                raf[controle].seek(raf[controle].length());
                raf[controle].writeInt(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], raf[controle]);
                }
                controle++;
            }
        }
        // calculo a quantidade de passadas que vou precisar no arquivo
        int passadas = (int) (Math.log(qtd / 3) / Math.log(2)) + 1;
        balanceadaComum(passadas, raf, 0, 2); // chamo a função de balancear de fato
        arq.close();
    }

    /**
     * balanceadaComum - Função que faz a ordenação desses segmentos
     * 
     * @param passadas - recebo como parametro a quantidade máxima de passadas que
     *                 farei nesse arquivo
     * @param tmpOld[] - temos o array de arquivos que vamos utilizar para recuperar
     *                 os registros que serão ordenados
     * @param passada  - Parte do processo ocorre recursivamente, logo precisamos
     *                 saber em qual passada no arquivo nos estamos
     * @param temp     - responsável por nos informar a numeração do proximo arquivo
     *                 temporario
     */
    public void balanceadaComum(int passadas, RandomAccessFile tmpOld[], int passada, int temp)
            throws Exception {
        RandomAccessFile tmpNew[] = new RandomAccessFile[2]; // novo array de arquivos que serea o destino apos a
                                                             // ordenação
        String part1 = "bd/temp";
        String part2 = ".db";

        boolean flag = false; // serve para parar o while em caso de fim do arquivo
        boolean first = true; // serve para sinalizar se é necessário ler o tamanho do bloco
        // j e k são nossos indices para os arquivos
        int j = 0;
        int k = j + 1;

        int controle = 0;
        boolean check = false;
        tmpOld[j].seek(0);
        tmpOld[k].seek(0);
        boolean arq = false; // serve para definir em qual arquivo iremos salvar o novo bloco ordenado
        // variaveis size serve para o controle da quantidade de registros em um
        // bloco
        int size = 0;
        int size2 = 0;

        if (passada < passadas) { // teste se não excedemos as passadas
            for (int i = 0; i < 2; i++) { // nomeamos os arquivos

                tmpNew[i] = new RandomAccessFile(part1 + temp + part2, "rw");
                temp++;
            }
            // leio o tamanho do bloco
            size = tmpOld[j].readInt();
            size2 = tmpOld[k].readInt();

            while (flag == false) {
                if (check == true) {
                    check = false;
                    size = tmpOld[j].readInt();
                    size2 = tmpOld[k].readInt();

                }
                if (first == false) {
                    tmpOld[j].seek(0);
                    tmpOld[k].seek(0);
                    size = tmpOld[j].readInt();
                    size2 = tmpOld[k].readInt();
                }
                if (arq == false) {
                    tmpNew[j].writeInt(size + size2); // escrevo a quantidade do proximo bloco
                } else {
                    tmpNew[k].writeInt(size + size2); // escrevo a quantidade do proximo bloco
                }
                // variaveis size serve para o controle da quantidade de registros lidos em um
                // bloco
                int controle1 = 0;
                int controle2 = 0;
                usuario user1 = new usuario();
                usuario user2 = new usuario();
                while ((controle1 + controle2) < (size + size2)) {
                    // se o primeiro bloco ja tiver chegado ao fim e ainda tem registros no bloco do
                    // outro arquivo, pegamos todos os registros restantes
                    if (controle1 == size && controle2 < size2) {
                        if (user2.getId() == -1) { // se o usuario for nulo, leio um registro
                            user2 = readUser(tmpOld[k].getFilePointer(), tmpOld[k]);
                        }

                        first = false;
                        if (arq == false) { // verifico em qual arquivo salvaremos esse bloco

                            insert(user2, tmpNew[j]); // salvamos o registro no arquivo
                            user2 = new usuario(); // zeramos o usuario
                        } else {

                            insert(user2, tmpNew[k]); // salvamos o registro no arquivo

                            user2 = new usuario(); // zeramos o usuario
                        }

                        controle2++; // incremento na variavel de controle do bloco do 2º arquivo
                    } else if (controle1 < size && controle2 == size2) {
                        // se o segundo bloco ja tiver chegado ao fim e ainda tem registros no bloco do
                        // outro arquivo, pegamos todos os registros restantes
                        if (user1.getId() == -1) { // verifico em qual arquivo salvaremos esse bloco
                            user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j]);
                        }

                        first = false;
                        if (arq == false) {

                            insert(user1, tmpNew[j]);
                            user1 = new usuario();
                        } else {

                            insert(user1, tmpNew[k]);
                            user1 = new usuario();
                        }
                        controle1++; // incremento na variavel de controle do bloco do 1º arquivo
                    } else if (controle1 < size && controle2 < size2) {
                        // se os dois blocos nao chegaram ao fim ainda
                        if (user1.getId() == -1) { // se nulo, leio um registro do arquivo
                            user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j]);

                        }
                        if (user2.getId() == -1) { // se nulo, leio um registro do arquivo

                            user2 = readUser(tmpOld[k].getFilePointer(), tmpOld[k]);

                        }
                        first = false;

                        if (user1.getId() < user2.getId()) { // verifico os id's dos ponteiros
                            if (arq == false) { // verifico o destino para salvar no arquivo

                                insert(user1, tmpNew[j]);
                                controle1++; // como inseri um registro do arquivo1, temos que incrementar em sua
                                             // variavel de controle
                                user1 = new usuario(); // zero o user usado
                            } else {
                                // repito o processo salvando no arquivo2
                                insert(user1, tmpNew[k]);
                                controle1++;
                                user1 = new usuario();
                            }
                        } else {
                            // repito o processo acima mas como peguei um registro do arquivo2, incremento
                            // na variavel controle2
                            if (arq == false) {

                                insert(user2, tmpNew[j]);
                                controle2++;
                                user2 = new usuario();

                            } else {

                                insert(user2, tmpNew[k]);
                                controle2++;
                                user2 = new usuario();
                            }

                        }
                    }
                }
                if (tmpOld[j].getFilePointer() < tmpOld[j].length()
                        && tmpOld[k].getFilePointer() >= tmpOld[k].length()) {
                    // se o arquivo1 ainda nao estiver esgotado mas o arquivo2 ja tiver sem blocos
                    // para comparação
                    size = tmpOld[j].readInt(); // pegamos o tamanho do bloco em questão
                    tmpNew[k].writeInt(size); // gravamos o tamanho em um arquivo destino
                    first = true;

                    while (controle < size) { // enquanto houver registros no bloco vamos ler e salvar no arquivo
                                              // destino
                        user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j]);
                        insert(user1, tmpNew[k]);
                        first = false;
                        controle++;
                        flag = true;
                    }
                } else if (tmpOld[j].getFilePointer() >= tmpOld[j].length()
                        && tmpOld[k].getFilePointer() < tmpOld[k].length()) {
                    // se o arquivo2 ainda nao estiver esgotado mas o arquivo1 ja tiver sem blocos
                    // para comparação
                    size = tmpOld[k].readInt(); // pegamos o tamanho do bloco em questão
                    tmpNew[k].writeInt(size); // gravamos o tamanho em um arquivo destino
                    first = true;
                    while (controle < size) {
                        // enquanto houver registros no bloco vamos ler e salvar no arquivo
                        // destino
                        user1 = readUser(tmpOld[k].getFilePointer(), tmpOld[k]);
                        insert(user1, tmpNew[k]);
                        first = false;
                        controle++;
                    }
                    flag = true;

                } else if (tmpOld[j].getFilePointer() < tmpOld[j].length()
                        && tmpOld[k].getFilePointer() < tmpOld[k].length()) {
                    // se ainda houver blocos nos dois arquivos vamos repetir nosso while
                    first = true; // first = true por que precisaremos ler o tamanho dos proximos blocos
                    controle = 0;
                    check = true;
                    arq = !arq; // mudamos a orientação de onde salvar os registros

                }
                if (tmpOld[j].getFilePointer() >= tmpOld[j].length()
                        && tmpOld[k].getFilePointer() >= tmpOld[k].length()) {
                    // caso os dois arquivos estejam zerados, vamos incrementar na passada
                    // e chamaremos recursivamente nossa função
                    first = true;
                    flag = true; // damos fim ao while
                    ++passada;

                    balanceadaComum(passadas, tmpNew, passada, temp);

                }

            }
        } else {
            // como chamamos a função recursivamente, ao retornar a pilha de execuções, e
            // voltarmos a primeira chamada, teremos um arquivo totalmente ordenado
            // Com isso vamos descobrir qual é este arquivo e iremos sobrescrever ele em
            // cima do nosso arquivo original de dados

            // Ao fim das execuções sempre teremos um arquivo temporario vazio, por isso
            // utilizamos o length
            if (tmpOld[j].length() > tmpOld[k].length()) { // se o arquivo par for o ordenado
                temp -= 2; // pegaremos seu numero exato
                File a = new File(part1 + temp + part2);
                File b = new File("bd/bd.db");
                a.renameTo(b); // Sobrescrevemos os arquivos
            } else if (tmpOld[j].length() < tmpOld[k].length()) { // se o arquivo impar for o ordenado
                temp -= 1; // pegaremos seu numero exato
                File a = new File(part1 + temp + part2);
                File b = new File("bd/bd.db");
                a.renameTo(b); // Sobrescrevemos os arquivos

            } else { // houve problemas kkk
                System.out.println("Deu ruim");
            }
            // Chamamos essa função para apagar os arquivos temporarios utilizados
            cleanTemps();

        }

    }

    /**
     * cleanTemps() - Usamos essa função para apagar os arquivos temporarios que
     * foram abertos
     * 
     * @throws Exception
     */
    public void cleanTemps() throws Exception {
        File a = new File("bd/"); // vou ao diretorio onde se encontram
        String lista[] = a.list(); // pego uma lista com o nome de todos os arquivos nessa pasta
        for (int i = 0; i < lista.length; i++) {
            if (lista[i].contains("temp")) { 
                // se na posição i dessa lista houver um arquivo que em seu nome tem a
                // palavra temp
                File b = new File("bd/" + lista[i]);
                b.delete(); // apagamos esse arquivo
            }

        }
    }

    public void insert(usuario user, RandomAccessFile arq) throws Exception {

        byte[] b;
        try {
            long size = arq.length();
            arq.seek(size);
            b = user.toByteArray();
            arq.writeByte(' ');
            arq.writeInt(b.length);
            arq.write(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public usuario readUser(long pointer, RandomAccessFile arq) throws Exception {
        usuario user = new usuario();
        if (pointer >= 0) {

            arq.seek(pointer);
            arq.readByte();
            arq.readInt();

            user.setID(arq.readInt());
            user.setNomeUsuario(arq.readUTF());
            user.setNome(arq.readUTF());

            int sizeEmails = arq.readByte();
            for (int i = 0; i < sizeEmails; i++) {
                user.setEmail(arq.readUTF());
            }
            user.setSenha(arq.readUTF());
            user.setCidade(arq.readUTF());
            String cpf = "";
            for (int i = 0; i < 11; i++) {
                cpf += (char) arq.readByte();
            }
            user.setCPF(cpf);
            user.setTransferencias(arq.readInt());
            user.setSaldo(arq.readInt());

        } else {
            System.out.println("O ID informado é inválido :(");
        }

        return (user);
    }
}
/*
 * 
 * Pseudocodigo
 * percorrer todo o arquivo testando se o registro é lapide
 * se nao for lapide pega o regisgtro e chama uma função pra transformar em
 * objeto
 * se for lapide de um skip bytes
 * 
 * depois de pegar 3 registros em memoria principal, ordenamos e jogamos esses
 * registros para o arquivo temp fazendo dele 1 bloco
 * repetimos o processo ate preencher todos os arquivos
 */
