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
    public void blocos() throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "r");
        RandomAccessFile temp1 = new RandomAccessFile("bd/temp1.db", "rw");
        RandomAccessFile temp2 = new RandomAccessFile("bd/temp2.db", "rw");
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

        usuario usersArray[] = new usuario[3];
        int j = 0;
        boolean flag = true;
        for (int i = 0; i < users.size(); i++) {
            if (j == 3) {

                j = 0;
                selecao(usersArray, usersArray.length);
                if (flag == true) {
                    flag = false;
                    long size = temp1.length();
                    temp1.seek(size);
                    temp1.writeShort(3);
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], temp1);
                    }
                } else {
                    flag = true;
                    long size = temp2.length();
                    temp2.seek(size);
                    temp2.writeShort(3);
                    for (int k = 0; k < usersArray.length; k++) {
                        insert(usersArray[k], temp2);
                    }
                }
            }
            usersArray[j] = users.get(i).clone();
            j++;
        }

        if (j <= 2) {
            selecao(usersArray, j);
            if (flag == true) {
                flag = false;
                long size = temp1.length();
                temp1.seek(size);
                temp1.writeShort(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], temp1);
                }
            } else {
                flag = true;
                long size = temp2.length();
                temp2.seek(size);
                temp2.writeShort(j);
                for (int k = 0; k < j; k++) {
                    insert(usersArray[k], temp2);
                }
            }
        }
        int passadas = (int) (Math.log(qtd / 3) / Math.log(2)) + 1;
        // balanceadaComum(passadas);
    }

    public void balanceadaComum(int passadas, int i, boolean flag, int qtd) throws Exception {
        RandomAccessFile temp1 = new RandomAccessFile("bd/temp1.db", "rw");
        RandomAccessFile temp2 = new RandomAccessFile("bd/temp2.db", "rw");
        RandomAccessFile temp3 = new RandomAccessFile("bd/temp3.db", "rw");
        RandomAccessFile temp4 = new RandomAccessFile("bd/temp4.db", "rw");
        ArrayList<usuario> users = new ArrayList<usuario>();
        int j = 0;
        if (flag == true) { // vou estar salvando os dados no temp3 e temp4
            int qtdBlocos1 = quantidadeBlocos(temp1);
            int qtdBlocos2 = quantidadeBlocos(temp2);
            int indexBloco1 = 0;
            int indexBloco2 = 0;
            while () {
                
            }
        } else{ // vou estar salvando os dados em temp1 e temp2

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
            user.printUser();
        } else {
            System.out.println("O ID informado é inválido :(");
        }

        arq.close();
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
