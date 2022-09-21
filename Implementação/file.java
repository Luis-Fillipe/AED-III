import java.io.*;
import java.util.Scanner;

public class file {
    RandomAccessFile arq;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    public int getId() throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        int id = -1;
        arq.seek(0);
        id = arq.readInt();
        id += 1;
        updateLastId(id);
        arq.close();
        return (id);
    }

    public void createFile() throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        arq.seek(0);
        arq.writeInt(0);
        arq.close();
    }

    public void updateLastId(int id) throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        arq.seek(0);
        arq.writeInt(id);
        arq.close();
    }

    public boolean checkUsername(String username) throws Exception {
        boolean flag = true;
        arq = new RandomAccessFile("bd/bd.db", "r");
        arq.seek(0);
        arq.skipBytes(4);
        if (arq.length() > 4) {
            int size = -1;
            while (flag == true && (arq.length() - arq.getFilePointer()) > 0) {

                if (arq.readByte() == 32) {
                    size = arq.readInt();
                    arq.skipBytes(4);
                    String nome = arq.readUTF();
                    int skip = (size - 6 - nome.length());
                    if (nome.equals(username)) {
                        flag = false;
                    } else {
                        arq.skipBytes(skip);
                    }
                } else {
                    size = arq.readInt();
                    arq.skipBytes(size);
                }
            }
        }

        arq.close();
        return (flag);
    }

    public boolean deleteUser(int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        System.out.println("Ponteiro: " + pointer);
        if (pointer > 0) {
            arq.seek(pointer);
            flag = true;
            arq.writeByte('*');
        }

        arq.close();
        return (flag);
    }

    public long getPointer(int id) throws Exception {
        boolean flag = false;
        long ponteiro = -1;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        arq.seek(0);
        int cabecalho = arq.readInt();
        if (id > cabecalho) {
            System.out.println("Este ID é inválido!");
            System.out.println("Motivo: ID maior do que o cabeçalho");
        } else {
            while (flag == false && (arq.length() - arq.getFilePointer()) > 0) {
                long start = arq.getFilePointer();
                byte lapide = arq.readByte();
                int size = arq.readInt();
                int idFile = arq.readInt();
                if (lapide == 42) {

                    arq.skipBytes(size - 4);

                } else {
                    if (idFile == id) {
                        ponteiro = start;
                    } else {
                        arq.skipBytes(size - 4);
                    }
                }
            }

        }

        // arq.close();
        return (ponteiro);
    }

    public boolean readUser(int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "r");
        long pointer = getPointer(id);
        if (pointer > 0) {
            arq.seek(pointer);
            flag = true;
            arq.readByte();
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
            user.printUser();
        }
        else{
            System.out.println("O ID informado é inválido :(");
        }

        arq.close();
        return (flag);
    }

    public boolean readUsers() throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "r");
        arq.seek(4);
        while ((arq.length() - arg.getFilePointer()) > 0) {
            char lapide = arq.readByte();
            if(lapide == ' '){
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
                user.printUser();
            }
        
        else{
            int size = arq.readInt()
            arq.seek(arq.getFilePointer + size);
        }
    }
        arq.close();
        return (flag);
    }

    public boolean transfer(int idTo, int idFrom, int valor) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointerTo = getPointePOuinr(idTo) + 1;
        long pointerFrom = getPointer(idFrom) + 1;

        if (pointerTo > 0 && pointerFrom > 0) {
            arq.seek(pointerTo);
            int sizeTo = arq.readInt();
            pointerTo += (sizeTo - 4);
            arq.seek(pointerTo);
            int TransferTo = arq.readInt();
            int saldoTo = arq.readInt();

            arq.seek(pointerFrom);
            int sizeFrom = arq.readInt();
            pointerFrom += (sizeFrom - 4);
            arq.seek(pointerFrom);
            int TransferFrom = arq.readInt();
            int saldoFrom = arq.readInt();

            if (saldoTo > valor) {
                saldoTo -= valor;
                saldoFrom += valor;
                arq.seek(pointerTo);
                arq.writeInt(++TransferTo);
                arq.writeInt(saldoTo);

                arq.seek(pointerFrom);
                arq.writeInt(++TransferFrom);
                arq.writeInt(saldoFrom);
                flag = true;
            } else {
                System.out.println("Este VALOR é inválido!");
                System.out.println("Motivo: Valor maior que o Saldo");
            }

            // pointerTo += (size - 4); para encontrar a quantidade de transferencias
            // pointerTo = pointerTo + (size+1); para encontrar o saldo

            arq.seek(pointerTo);

        }else{
            System.out.println("Um dos ID's informados é inválido :(");
        }

        arq.close();
        return (flag);
    }

    public void insert(usuario user) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "rw");
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

    public usuario getObj(int id) throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        usuario user = new usuario();
        if (pointer > 0) {
            arq.seek(pointer + 5); // (pointer + 5) me deixa no id

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
        }
        return (user);
    }

    public boolean editName(String newName, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            String oldName = arq.readUTF();
            if (newName.length() > oldName.length() || newName.length() < oldName.length()) {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setNome(newName);
                insert(user);

            } else {
                arq.seek(pointer + 9);
                arq.readUTF();
                arq.writeUTF(newName);
            }
        }
        arq.close();
        return (flag);
    }

    public boolean editUsername(String newName, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0 && checkUsername(newName) == true) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            String oldName = arq.readUTF();

            if (newName.length() > oldName.length() || newName.length() < oldName.length()) {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setNomeUsuario(newName);
                insert(user);

            } else {
                arq.seek(pointer + 9);
                arq.readUTF();
                arq.writeUTF(newName);
            }
        }
        arq.close();
        return (flag);
    }

    public boolean editPassword(String newPassword, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            arq.readUTF();
            int sizeEmails = arq.readByte();
            for (int i = 0; i < sizeEmails; i++) {
                arq.readUTF();
            }
            long breakpoint = arq.getFilePointer();
            String oldPassword = arq.readUTF();

            if (newPassword.length() == oldPassword.length()) {
                arq.seek(breakpoint);
                arq.writeUTF(newPassword);
            } else {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setSenha(newPassword);
                insert(user);
            }

        }
        arq.close();
        return (flag);
    }

    public boolean editCity(String newCity, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            arq.readUTF();
            int sizeEmails = arq.readByte();
            for (int i = 0; i < sizeEmails; i++) {
                arq.readUTF();
            }
            arq.readUTF();
            long breakpoint = arq.getFilePointer();
            String oldCity = arq.readUTF();

            if (newCity.length() == oldCity.length()) {
                arq.seek(breakpoint);
                arq.writeUTF(newCity);
            } else {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setCidade(newCity);
                insert(user);
            }

        }
        arq.close();
        return (flag);
    }

    public boolean editEmail(String newEmail, int id, int idEmail) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            arq.readUTF();
            int sizeEmails = arq.readByte();
            for (int i = 0; i < sizeEmails; i++) {
                if (idEmail == i) {
                    long breakpoint = arq.getFilePointer();
                    String oldEmail = arq.readUTF();
                    if (newEmail.length() == oldEmail.length()) {
                        arq.seek(breakpoint);
                        arq.writeUTF(newEmail);
                    } else {
                        usuario user = getObj(id);
                        arq.seek(pointer);
                        arq.writeByte('*');
                        user.setEmail(newEmail, i);
                        insert(user);
                    }
                }
            }

        }
        arq.close();
        return (flag);
    }

    public boolean choiceEmail(int id) throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        int sizeEmails = -1;
        if (pointer > 0) {
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            arq.readUTF();
            sizeEmails = arq.readByte();
            int x = 1;
            System.out.println("Escolha o email que deseja alterar");
            for (int i = 0; i < sizeEmails; i++) {
                
                    long breakpoint = arq.getFilePointer();
                    String oldEmail = arq.readUTF();
                    System.out.println(x+". "+oldEmail);
                    x++;
                }
            
            int choice = sc.nextInt();
            while (choice <= 0 && choice > (sizeEmails+1)) {
                System.out.println("Opção Inválida!\nDigite novamente: ");
                choice = sc.nextInt();
            }
            System.out.println("Digite seu novo email: ");
            String newEmail = sc.nextLine();
            while (newEmail.length() < 10) {
                System.out.println("Seu email não segue os padrões, por favor digite novamente: ");
                newEmail = sc.nextLine();
            }
            editEmail(newEmail, id, --choice);
            flag = true;
        }
        arq.close();
        return(flag);
    }
}
