import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.plaf.synth.SynthScrollPaneUI;

public class lista {
    RandomAccessFile lista;

    public void inserir(String nome, int id) throws IOException {
        String nome1 = "Luis Fillipe";
        String nomes[] = nome1.split(" ");
        long ponteiro = -1;
        lista = new RandomAccessFile("bd/listaNomes.db", "rw");
        if (lista.length() < 1) { // se a lista tiver vazia
            for (int i = 0; i < nomes.length; i++) {
                lista.writeUTF(nomes[i]);
                lista.writeByte(1);
                lista.writeInt(id);
                lista.seek(lista.getFilePointer() + 32);
                lista.writeLong(-1);
            }
        } else { // temos nomes na lista já

            for (int i = 0; i < nomes.length; i++) {

                ponteiro = onList(nomes[i], lista);
                if (ponteiro != -1) {
                    if (lista.readByte() < 9) {
                        add(id, lista, ponteiro);
                    } else { // tem o nome mas ja ta cheio ai
                        newSpace(nomes[i], 9, lista, ponteiro);
                    }
                }

            }

        }
    }

    public long onList(String nome, RandomAccessFile lista) throws IOException {
        long ponteiro = -1;
        boolean flag = false;
        lista.seek(0);
        System.out.println("tamanho "+lista.length());
        while ((lista.length() - lista.getFilePointer()) > 0) {
            System.out.println("resultado "+ (lista.length() - lista.getFilePointer()));
            String nomeLido = lista.readUTF();
            System.out.println(nomeLido);
            if (nomeLido.equals(nome)) {
                System.out.println("entrei");
                ponteiro = lista.getFilePointer();
                
            } else {
                System.out.println("entrei");
                lista.seek(lista.getFilePointer()+44);
                System.out.println("estopu em "+lista.getFilePointer());
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
        System.out.println("write in "+ponteiro+36);
        lista.writeLong(createSpace(nome, lista, id));

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
}