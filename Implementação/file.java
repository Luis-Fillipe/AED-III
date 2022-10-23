import java.io.*;
import java.security.cert.LDAPCertStoreParameters;
import java.util.Scanner;

public class file {
    RandomAccessFile arq;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    /*
     * getID() - Função para verificar o ID presente no cabeçalho do arquivo e
     * retornar o valor encontrado
     * 
     */
    public int getId() throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        int id = -1;
        arq.seek(0);
        id = arq.readInt();
        id += 1;

        arq.close();
        return (id);
    }

    /*
     * createFile() - Caso não tenhamos nosso arquivo criado, este método criamos o
     * arquivo e setamos o cabeçalho como 0
     */
    public void createFile() throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        arq.seek(0);
        arq.writeInt(0);
        arq.close();
    }

    /*
     * updateLastID - Recebe o ultimo ID inserido como parametro e atualiza o
     * cabeçalho do arquivo
     */
    public void updateLastId(int id) throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "rw");
        arq.seek(0);
        arq.writeInt(id);
        arq.close();
    }

    /*
     * checkUsername - Verifica se o username que o usuário deseja colocar em seu
     * cadastro ja existe na base de dados
     * Return - False caso nao exista e true caso ja exista
     */
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

        return (flag);
    }

    /*
     * deleteUSer() - Função para apagar um usuario recebendo seu ID como parametro
     * return - False caso nao tenha apagado e true caso tenha apagado
     */
    public boolean deleteUser(int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        // System.out.println("Ponteiro: " + pointer);
        if (pointer > 0) {
            arq.seek(pointer);
            flag = true;
            arq.writeByte('*');
        }
        
        arq.close();
        return (flag);
    }

    /*
     * getPointer() - Função para encontrar a posição inicial de um registro na base
     * de dados tendo como parametro o ID fornecido
     * return - -1 caso nao tenha encontrado ou o ponteiro caso tenha encontrado
     */
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

    /*
     * readUSer() - Função para trazer um usuario para memoria principal para
     * exibi-lo na tela tendo recebido o ID como parametro
     * return - False caso nao tenha sido encontrado e true caso tenha sido
     * encontrado
     */
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
        } else {
            System.out.println("O ID informado é inválido :(");
        }

        arq.close();
        return (flag);
    }

    /*
     * readUsers() - Função responsavel por ler todo o arquivo de dados buscando os
     * registros válidos presentes no arquivo
     */
    public boolean readUsers() throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "r");
        arq.seek(4);
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
                user.printUser();
            }

            else {
                int size = arq.readInt();
                arq.seek(arq.getFilePointer() + size);
            }
        }
        arq.close();
        return (flag);
    }

    /*
     * transfer() - Função responsável por fazer a transferencia entre 2 contas
     * cadastradas no sistema
     */
    public boolean transfer(int idTo, int idFrom, int valor) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointerTo = getPointer(idTo) + 1;
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

        } else {
            System.out.println("Um dos ID's informados é inválido :(");
        }

        arq.close();
        return (flag);
    }

    /*
     * inser() - Função responsável por inserir o obj user no nosso arquivo de dados
     */
    public long insert(usuario user) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "rw");
        byte[] b;
        long ponteiro = -1;
        try {
            arq.seek(arq.length());
            b = user.toByteArray();
            ponteiro = arq.getFilePointer();
            arq.writeByte(' ');
            arq.writeInt(b.length);
            arq.write(b);
            updateLastId(user.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return(ponteiro);
    }

    public void edit(usuario user) throws Exception {
        RandomAccessFile arq = new RandomAccessFile("bd/bd.db", "rw");
        byte[] b;
        hash hash = new hash();
        try {
            long size = arq.length();
            arq.seek(size);
            b = user.toByteArray();
            hash.edit(user.getId(), arq.getFilePointer());
            arq.writeByte(' ');
            arq.writeInt(b.length);
            arq.write(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * getObj() - Esta função recebe o ID do usuario como parametro e busca no
     * arquivo de dados sua ocorrencia, caso seja encontrado ela
     */

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

    /*
     * editName() - Função para editar o nome do id informado
     */
    public boolean editName(String newName, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            lista listaNome = new lista();
            flag = true;
            arq.seek(pointer + 9); // (pointer + 8) me deixa na lapide
            arq.readUTF();
            String oldName = arq.readUTF();
            listaNome.delete(oldName, id, false, "");
            listaNome.inserir(newName, id, false, "");
            if (newName.length() > oldName.length() || newName.length() < oldName.length()) {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setNome(newName);
                edit(user);

            } else {
                arq.seek(pointer + 9);
                arq.readUTF();
                arq.writeUTF(newName);
            }
        }
        arq.close();
        return (flag);
    }

    /*
     * editUsername() - FUnção na qual edita o nome de usuário do id informado
     * testando se o username ja nao foi utilizado
     */
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
                edit(user);

            } else {
                arq.seek(pointer + 9);
                arq.readUTF();
                arq.writeUTF(newName);
            }
        }
        arq.close();
        return (flag);
    }

    /*
     * editPassword() - Função para editar a senha mediante o id informado
     */
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
                edit(user);
            }

        }
        arq.close();
        return (flag);
    }

    /*
     * editCity() - Função na qual recebe o novo nome da cidade e altera no arquivo
     * de dados com o id informado
     */
    public boolean editCity(String newCity, int id) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "rw");
        long pointer = getPointer(id);
        if (pointer > 0) {
            lista listaCidade = new lista();
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
            listaCidade.deleteCidade(oldCity, id);
            listaCidade.inserirCidade(newCity, id);
            if (newCity.length() == oldCity.length()) {
                arq.seek(breakpoint);
                arq.writeUTF(newCity);
            } else {
                usuario user = getObj(id);
                arq.seek(pointer);
                arq.writeByte('*');
                user.setCidade(newCity);
                edit(user);
            }

        }
        arq.close();
        return (flag);
    }

    /*
     * editEmail() - Função pela qual realmente fará a alteração no arquivo do email
     * selecionado
     */
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
                        edit(user);
                    }
                }
            }

        }
        arq.close();
        return (flag);
    }

    /*
     * choiceEmail() - Função com o objetivo de disponibilizar ao usuario o email
     * que deseja editar
     */
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
                System.out.println(x + ". " + oldEmail);
                x++;
            }

            int choice = sc.nextInt();
            while (choice <= 0 && choice > (sizeEmails + 1)) {
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
        return (flag);
    }

    public boolean readUser(long pointer) throws Exception {
        boolean flag = false;
        arq = new RandomAccessFile("bd/bd.db", "r");
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
        } else {
            System.out.println("O ID informado é inválido :(");
        }

        arq.close();
        return (flag);
    }
}
