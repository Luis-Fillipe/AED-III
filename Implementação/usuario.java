
import java.io.*;
import java.util.ArrayList;

class usuario {
    private int idConta;
    private String nome;
    private ArrayList<String> email = new ArrayList<>();
    private String nomeUsuario;
    private String senha;
    private String cpf;
    private String cidade;
    private int transferencias;
    private int saldoConta;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    public usuario() {
        idConta = -1;
        nome = "";
        nomeUsuario = "";
        senha = "";
        cpf = "";
        cidade = "";
        transferencias = 0;
        saldoConta = 0;
    }

    public usuario(int id, String nome, ArrayList<String> email, String nomeUsuario, String senha, String cpf,
            String cidade,
            int saldo) throws Exception {
        setID();
        setNome(nome);
        setEmail(email);
        setNomeUsuario(nomeUsuario);
        setSenha(senha);
        setCPF(cpf);
        setCidade(cidade);
        setSaldo(saldo);
        setTransferencias(transferencias);
    }

    public void setID() throws Exception {
        exists();
        // System.out.println(flag);

        file arq = new file();
        int id = arq.getId();
        this.idConta = id;

    }

    public void setID(int id) throws Exception {

        this.idConta = id;

    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setSaldo(int saldo) {
        this.saldoConta = saldo;
    }

    public void setCPF(String cpf) {

        this.cpf = cpf;
    }

    public void setEmail(ArrayList<String> email) {
        this.email.addAll(email);
    }

    public void setEmail(String email) {
        this.email.add(email);
    }

    public void setEmail(String email, int i) {
        this.email.set(i, email);
    }

    public void setTransferencias(int transferencias) {
        this.transferencias = transferencias;
    }

    public int getId() {
        return (this.idConta);
    }
    public String getNome() {
        return (this.nome);
    }

    public byte[] toByteArray() throws IOException {
        dos.writeInt(this.idConta);
        dos.writeUTF(this.nomeUsuario);
        dos.writeUTF(this.nome);
        dos.write(email.size());
        for (String str : email) {
            dos.writeUTF(str);
        }
        dos.writeUTF(this.senha);
        dos.writeUTF(this.cidade);
        dos.writeBytes(this.cpf);
        dos.writeInt(this.transferencias);
        dos.writeInt(this.saldoConta);
        return baos.toByteArray();
    }

    public void exists() throws Exception {

        File file = new File("bd/bd.db");
        file arq = new file();

        if (!(file.exists() && !file.isDirectory())) {
            arq.createFile();
        }
    }

    public void printUser() {
        System.out.println("ID: "+idConta);
        System.out.println("Nome: " + nome);
        System.out.println("Nome de usuário: " + nomeUsuario);
        //System.out.println("Senha: "+senha);
        System.out.println("Cidade: " + cidade);
        System.out.println("Emails: " + email);
        System.out.println("CPF: " + cpf);
        System.out.println("Transferencias realizadas: " + transferencias);
        System.out.println("Saldo atual: " + saldoConta);
    }

    public usuario clone() {
		usuario cloned = new usuario();

		cloned.idConta = this.idConta;
		cloned.nome = this.nome;
		cloned.nomeUsuario = this.nomeUsuario;
		cloned.senha = this.senha;
		cloned.cidade = this.cidade;
		cloned.email = this.email;
		cloned.cpf = this.cpf;
		cloned.transferencias = this.transferencias;
		cloned.saldoConta = this.saldoConta;

		return cloned;
	}

    public String getCidade() {
        return cidade;
    }

}
