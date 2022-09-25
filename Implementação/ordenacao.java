import java.io.IOException;
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
     * balanceada() - Utilizaremos 3 registros e 2 caminhos para realizar a
     * intercalação balanceada
     */
    public void blocos(int registros, int caminhos) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "r");
        RandomAccessFile raf[] = new RandomAccessFile[caminhos];
        String part1 = "bd/temp";
        String part2 = ".db";
        int qtdFile = 0;
        for (int i = 0; i < raf.length; i++) {
            raf[i] = new RandomAccessFile(part1 + i + part2, "rw");
            qtdFile++;
        }

        ArrayList<usuario> users = new ArrayList<usuario>();
        arq.seek(4);
        int qtd = 0;
        while ((arq.length() - arq.getFilePointer()) > 0) {
            char lapide = (char) arq.readByte();
            if (lapide == ' ') {
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
                users.add(user);
                qtd++;
            } else {
                int size = arq.readInt();
                arq.seek(arq.getFilePointer() + size);
            }
        }

        usuario usersArray[] = new usuario[registros];
        int j = 0;
        boolean flag = true;
        int controle = 0;
        for (int i = 0; i < users.size(); i++) {
            if (j == registros) {

                j = 0;
                selecao(usersArray, usersArray.length);
                if (controle < caminhos) {
                    raf[controle].seek(raf[controle].length());
                    raf[controle].writeShort(registros);
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], raf[controle]);
                    }
                    controle++;
                } else {
                    controle = 0;
                    raf[controle].seek(raf[controle].length());
                    raf[controle].writeShort(registros);
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], raf[controle]);
                    }
                    controle++;
                }
            }
            usersArray[j] = users.get(i).clone();
            j++;
        }

        if (j <= registros) {
            selecao(usersArray, j);
            if (controle < caminhos) {
                raf[controle].seek(raf[controle].length());
                raf[controle].writeShort(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], raf[controle]);
                }
                controle++;
            } else {
                controle = 0;
                raf[controle].seek(raf[controle].length());
                raf[controle].writeShort(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], raf[controle]);
                }
                controle++;
            }
        }
        int passadas = (int) (Math.log(qtd / 3) / Math.log(2)) + 1;
        balanceadaComum(passadas, raf, registros, caminhos, 0, qtdFile);
    }

    public void balanceadaComum(int passadas, RandomAccessFile tmpOld[], int registros, int caminhos, int passada,
            int qtdFile)
            throws Exception {
        RandomAccessFile tmpNew[] = new RandomAccessFile[caminhos];
        String part1 = "bd/temp";
        String part2 = ".db";
        
        for (int i = 0; i < tmpNew.length; i++) {
            tmpNew[i] = new RandomAccessFile(part1 + (qtdFile) + part2, "rw");
            qtdFile++;
        }
        ArrayList<usuario> users = new ArrayList<usuario>();
        boolean flag = false;
        boolean first = true;
        int j = 0;
        int k = j + 1;
        int controle = 0;
        boolean check = false;
        tmpOld[j].seek(0);
        tmpOld[k].seek(0);
        boolean arq = false;
        int size = 0;
        int size2 = 0;
        if (passada < passadas) {

            size = tmpOld[j].readShort();
            size2 = tmpOld[k].readShort();

            //System.out.println("arq 1: " + size);
            //System.out.println("arq 2 é aqui: " + size2);
            while (flag == false) {
                if (check == true) {
                    check = false;
                    size = tmpOld[j].readShort();
                    size2 = tmpOld[k].readShort();

                   // System.out.println("arq 1: " + size);
                    //System.out.println("arq 2: " + size2);
                }
                if (first == false) {
                    //System.out.println(tmpOld[j].getFilePointer());
                    size = tmpOld[j].readShort();
                    size2 = tmpOld[k].readShort();
                }
                if (arq == false) {
                    tmpNew[j].writeShort(size + size2);
                } else {
                    tmpNew[k].writeShort(size + size2);
                }

                int controle1 = 0;
                int controle2 = 0;
                usuario user1 = new usuario();
                usuario user2 = new usuario();
                while ((controle1 + controle2) < (size + size2)) {
                    if (controle1 == size && controle2 < size2) {
                        if (user2.getId() == -1) {
                            user2 = readUser(tmpOld[k].getFilePointer(), tmpOld[k], first);
                        }

                        first = false;
                        if (arq == false) {
                            insert(user2, tmpNew[j]);
                            user2 = new usuario();
                        } else {
                            insert(user2, tmpNew[k]);
                            user2 = new usuario();
                        }

                        controle2++;
                    } else if (controle1 < size && controle2 == size2) {
                        if (user1.getId() == -1) {
                            user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j], first);
                        }

                        first = false;
                        if (arq == false) {
                            insert(user1, tmpNew[j]);
                            user1 = new usuario();
                        } else {
                            insert(user1, tmpNew[k]);
                            user1 = new usuario();
                        }
                        controle1++;
                    } else if (controle1 < size && controle2 < size2) {
                        if (user1.getId() == -1) {
                            user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j], first);
                        }
                        if (user2.getId() == -1) {
                            user2 = readUser(tmpOld[k].getFilePointer(), tmpOld[k], first);
                        }
                        first = false;
                        if (user1.getId() < user2.getId()) {
                            if (arq == false) {
                                insert(user1, tmpNew[j]);
                                controle1++;
                                user1 = new usuario();
                            } else {
                                insert(user1, tmpNew[k]);
                                controle1++;
                                user1 = new usuario();
                            }
                        } else {
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
                        // controle += 2;
                    }
                }
                //System.out.println("sai");
                if (tmpOld[j].getFilePointer() < tmpOld[j].length()
                        && tmpOld[k].getFilePointer() >= tmpOld[k].length()) {
                    
                    
                    while (controle < size) {
                        if (user1.getId() == -1) {
                            System.out.println(tmpOld[j].getFilePointer());
                            user1 = readUser(tmpOld[j].getFilePointer(), tmpOld[j], first);
                        } else {
                            insert(user1, tmpNew[j]);
                            first = false;
                            controle++;
                        }
                    }
                } else if (tmpOld[j].getFilePointer() >= tmpOld[j].length()
                        && tmpOld[k].getFilePointer() < tmpOld[k].length()) {
                    size = tmpOld[k].readShort();
                    tmpNew[k].writeShort(size);
                    first = true;
                    while (controle < size) {
                        user1 = readUser(tmpOld[k].getFilePointer(), tmpOld[k], first);
                        insert(user1, tmpNew[k]);
                        first = false;
                        controle++;
                    }

                }
                if (tmpOld[j].getFilePointer() < tmpOld[j].length()
                        && tmpOld[k].getFilePointer() < tmpOld[k].length()) {

                    first = true;
                    controle = 0;
                    check = true;
                    arq = !arq;
                    //System.out.println("VOU PRO SEGUNDO ARQUIVO " + arq);

                } else {

                    first = true;

                    //System.out.println("entrei2");
                    flag = true;
                    ++passada;
                    qtdFile += 2;
                    balanceadaComum(passadas, tmpNew, registros, caminhos, passada, qtdFile);

                }

            }
        }else {
            tmpOld[j].seek(0);
            int sizeBlock = tmpOld[j].readShort();
            for (int i = 0; i < sizeBlock; i++) {
                usuario user = readUser(tmpOld[j].getFilePointer(), tmpOld[j], true);
                user.printUser();
            }
        }

    }

    public int quantidadeBlocos(RandomAccessFile arq) throws IOException {
        int qtd = 0;
        while ((arq.length() - arq.getFilePointer()) > 0) {
            int sizebloco = arq.readInt();
            for (int i = 0; i < sizebloco; i++) {
                arq.readByte();
                int size = arq.readInt();
                arq.skipBytes(size);
            }
            qtd++;
        }

        return (qtd);
    }

    public void insert(usuario user, RandomAccessFile arq) throws Exception {

        byte[] b;
        int len;
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

    public void insert(ArrayList<usuario> users, RandomAccessFile arq) throws Exception {

        byte[] b;
        int len;
        arq.writeInt(users.size());
        for (int i = 0; i < users.size(); i++) {
            try {
                long size = arq.length();
                arq.seek(size);
                b = users.get(i).toByteArray();
                arq.writeByte(' ');
                arq.writeInt(b.length);
                arq.write(b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public usuario readUser(long pointer, RandomAccessFile arq, boolean first) throws Exception {
        usuario user = new usuario();
        // System.out.println(pointer);
        if (pointer >= 0) {

            arq.seek(pointer);
            arq.readByte();
            arq.readInt();

            user.setID(arq.readInt());
            user.setNomeUsuario(arq.readUTF());
            user.setNome(arq.readUTF());
            // System.out.println(user.getNome());
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

            // user.printUser();
        } else {
            System.out.println("O ID informado é inválido :(");
        }

        // arq.close();
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
