import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class lista {
    RandomAccessFile lista;
    public void inserir(String nome, int id) throws IOException{
        String nome1 = "Luis Fillipe ";
        String nomes [] = nome1.split(" ");
        lista = new RandomAccessFile("bd/listaNomes.db", "rw");
        if (lista.length() < 1). { // se a lista tiver vazia
            for (int i = 0; i < nomes.length; i++) {
                lista.writeUTF(nomes[i]);
                lista.seek(lista);
                lista.writeLong(-1);
            }
            lista.skipBytes(49);
            lista.writeLong(-1);
        }
    }
}