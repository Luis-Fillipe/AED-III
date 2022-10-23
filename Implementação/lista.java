
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

public class lista {
    RandomAccessFile listaNome;
    RandomAccessFile listaCidade;

    public void inserir(String nome, int id, boolean flag, String cidade) throws IOException {
        String nomes[] = nome.split(" ");
        long ponteiro = -1;
        listaNome = new RandomAccessFile("bd/listaNomes.db", "rw");
        listaCidade = new RandomAccessFile("bd/listaCidades.db", "rw");
        if (listaNome.length() < 1) { // se a listaNome tiver vazia
            for (int i = 0; i < nomes.length; i++) {
                listaNome.writeUTF(nomes[i]);
                listaNome.writeByte(1);
                listaNome.writeInt(id);
                listaNome.seek(listaNome.getFilePointer() + 32);
                listaNome.writeLong(-1);
            }
            if (flag == true) {
                listaCidade.writeUTF(cidade);
                listaCidade.writeByte(1);
                listaCidade.writeInt(id);
                listaCidade.seek(listaCidade.getFilePointer() + 32);
                listaCidade.writeLong(-1);
            }
        } else { // temos nomes na listaNome já

            for (int i = 0; i < nomes.length; i++) {

                ponteiro = onList(nomes[i], listaNome);
                if (ponteiro != -1) {
                    listaNome.seek(ponteiro);
                    if (listaNome.readByte() < 9) {
                        add(id, listaNome, ponteiro);
                    } else { // tem o nome mas ja ta cheio ai
                        newSpace(nomes[i], 9, listaNome, ponteiro);
                    }
                }
            }
            if (flag == true) {
                ponteiro = onList(cidade, listaCidade);
                if (ponteiro != -1) {
                    listaCidade.seek(ponteiro);
                    if (listaCidade.readByte() < 9) {
                        add(id, listaCidade, ponteiro);
                    } else { // tem o nome mas ja ta cheio ai
                        newSpace(cidade, id, listaCidade, ponteiro);
                    }
                }
            }

        }
    }

    public long onList(String nome, RandomAccessFile lista) throws IOException {
        long ponteiro = -1;
        lista.seek(0);
        while ((lista.length() - lista.getFilePointer()) > 0) {
            String nomeLido = lista.readUTF();
            if (nomeLido.equals(nome)) {

                ponteiro = lista.getFilePointer();
                if (lista.readByte() < 9) {

                    lista.seek(lista.length() + 1);
                } else {
                    lista.seek(lista.getFilePointer() + 44);
                }

            } else {

                lista.seek(lista.getFilePointer() + 45);

            }
        }
        return ponteiro;
    }

    public void add(int id, RandomAccessFile lista, long ponteiro) throws IOException {

        lista.seek(ponteiro);
        int qtd = lista.readByte();
        lista.seek(ponteiro);
        lista.writeByte(qtd + 1);
        lista.seek(lista.getFilePointer() + (qtd * 4));
        lista.writeInt(3);

    }

    public void newSpace(String nome, int id, RandomAccessFile lista, long ponteiro) throws IOException {

        lista.seek(ponteiro + 36);
        long endereco = createSpace(nome, lista, id);
        lista.seek(ponteiro + 37);
        lista.writeLong(endereco);

    }

    public long createSpace(String nome, RandomAccessFile lista, int id) throws IOException {
        long ponteiro = lista.length();
        lista.seek(ponteiro);
        lista.writeUTF(nome);
        lista.writeByte(1);
        lista.writeInt(id);
        lista.seek(lista.getFilePointer() + 32);
        lista.writeLong(-1);
        return ponteiro;
    }

    // Pesquisa

    public ArrayList<Integer> searchNome(String nome) throws IOException {
        listaNome = new RandomAccessFile("bd/listaNomes.db", "rw");
        ArrayList<Long> ponteiros = new ArrayList<Long>();

        ponteiros = onList2(nome, listaNome);

        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < ponteiros.size(); i++) {
            ids = getIds(ponteiros.get(i), ids, listaNome);
        }
        return (ids);
    }

    public ArrayList<Integer> searchCidade(String Cidade) throws IOException {
        listaCidade = new RandomAccessFile("bd/listaCidades.db", "rw");
        ArrayList<Long> ponteiros = new ArrayList<Long>();

        ponteiros = onList2(Cidade, listaCidade);
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < ponteiros.size(); i++) {
            ids = getIds(ponteiros.get(i), ids, listaCidade);
        }
        return (ids);
    }

    public ArrayList<Long> onList2(String nome, RandomAccessFile lista) throws IOException {
        ArrayList<Long> ponteiros = new ArrayList<Long>();
        lista.seek(0);
        while ((lista.length() - lista.getFilePointer()) > 0) {
            String nomeLido = lista.readUTF();
            if (nomeLido.equals(nome)) {

                ponteiros.add(lista.getFilePointer());

                lista.seek(lista.getFilePointer() + 37);
                long nextPointer = lista.readLong();
                if (nextPointer == -1) {
                    lista.seek(lista.length() + 10);
                } else {
                    lista.seek(nextPointer);
                }

            } else {

                lista.seek(lista.getFilePointer() + 45);

            }
        }
        return ponteiros;
    }

    public ArrayList<Integer> getIds(long ponteiro, ArrayList<Integer> ids, RandomAccessFile lista) throws IOException {
        lista.seek(ponteiro);
        int qtd = lista.readByte();
        for (int i = 0; i < qtd; i++) {
            ids.add(lista.readInt());
        }
        return ids;
    }

    // deletar um id
    public void delete(String nome, int id, boolean flag, String cidade) throws IOException {
        listaNome = new RandomAccessFile("bd/listaNomes.db", "rw");
        ArrayList<Long> ponteiros = new ArrayList<Long>();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        String nomes[] = nome.split(" ");
        for (int i = 0; i < nomes.length; i++) {
            ponteiros.addAll(onList2(nomes[i], listaNome));
        }
        if (ponteiros.size() == 1) {
            ids = getIds(ponteiros.get(0), ids, listaNome);
            ids.remove(id);
            Collections.sort(ids);
            listaNome.seek(ponteiros.get(0));
            listaNome.writeByte(ids.size());
            for (int i = 0; i < 9; i++) {
                if (i < ids.size()) {
                    listaNome.writeInt(ids.get(i));
                } else {
                    listaNome.writeInt(0);
                }
            }
        } else if (ponteiros.size() == 0) {

            System.out.println("Deu ruim");
        } else {

            for (int i = 0; i < ponteiros.size(); i++) {
                ids = getIds(ponteiros.get(i), ids, listaNome);
                if (ids.contains(id)) {
                    ids.remove(id);
                    Collections.sort(ids);
                    listaNome.seek(ponteiros.get(i));
                    listaNome.writeByte(ids.size());
                    for (int j = 0; j < 9; j++) {
                        if (j < ids.size()) {
                            listaNome.writeInt(ids.get(j));
                        } else {
                            listaNome.writeInt(0);
                        }
                    }

                }
                ids.clear();
            }
        }
        if (flag == true) {
            deleteCidade(cidade, id);
        }
    }

    public void deleteCidade(String cidade, int id) throws IOException {
        listaCidade = new RandomAccessFile("bd/listaCidades.db", "rw");
        ArrayList<Long> ponteiros = new ArrayList<Long>();
        ArrayList<Integer> ids = new ArrayList<Integer>();

        ponteiros.addAll(onList2(cidade, listaCidade));

        if (ponteiros.size() == 1) {
            ids = getIds(ponteiros.get(0), ids, listaCidade);
            ids.remove(id);
            Collections.sort(ids);
            listaCidade.seek(ponteiros.get(0));
            listaCidade.writeByte(ids.size());
            for (int i = 0; i < 9; i++) {
                if (i < ids.size()) {
                    listaCidade.writeInt(ids.get(i));
                } else {
                    listaCidade.writeInt(0);
                }
            }
        } else if (ponteiros.size() == 0) {
            
            System.out.println("Deu ruim");
        } else {

            for (int i = 0; i < ponteiros.size(); i++) {
                ids = getIds(ponteiros.get(i), ids, listaCidade);
                if (ids.contains(id)) {
                    ids.remove(id);
                    Collections.sort(ids);
                    listaCidade.seek(ponteiros.get(i));
                    listaCidade.writeByte(ids.size());
                    for (int j = 0; j < 9; j++) {
                        if (j < ids.size()) {
                            listaCidade.writeInt(ids.get(j));
                        } else {
                            listaCidade.writeInt(0);
                        }
                    }

                }
                ids.clear();
            }
        }
    }

    public void inserirCidade(String cidade, int id) throws IOException {
        long ponteiro = -1;
        listaCidade = new RandomAccessFile("bd/listaCidades.db", "rw");
        if (listaNome.length() < 1) { // se a listaNome tiver vazia
            listaCidade.writeUTF(cidade);
            listaCidade.writeByte(1);
            listaCidade.writeInt(id);
            listaCidade.seek(listaCidade.getFilePointer() + 32);
            listaCidade.writeLong(-1);
        } else { // temos nomes na listaNome já

            ponteiro = onList(cidade, listaCidade);
            if (ponteiro != -1) {
                listaCidade.seek(ponteiro);
                if (listaCidade.readByte() < 9) {
                    add(id, listaCidade, ponteiro);
                } else { // tem o nome mas ja ta cheio ai
                    newSpace(cidade, id, listaCidade, ponteiro);
                }
            }
        }

    }
}
