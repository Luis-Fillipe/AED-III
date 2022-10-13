import java.io.IOException;
import java.io.RandomAccessFile;

public class arvore {
    RandomAccessFile arvore;

    public void insert(int id, long endereco) throws IOException, Exception {
        arvore = new RandomAccessFile("bd/arvore.db", "rw");
        if (arvore.length() == 0) {
            arvore.writeLong(8);
            createFile(arvore, id, endereco);
            System.out.println(arvore.length());
        } else if (arvore.length() == 98) {
            System.out.println("continuo entrando");
            arvore.seek(8);
            int quantidade = arvore.readByte();
            arvore.skipBytes(1); // skip o booleano
            no no = new no();
            no.createFile();
            no.quantidade = quantidade;
            no.folha = true;
            int i = 0;
            
            while (i < quantidade) {
                
                no.pointers[i] = arvore.readLong();
                no.ids[i] = arvore.readInt();
                no.adress[i] = arvore.readLong();
                i++;
            }
            
            no.quantidade = i + 1;
            no.pointers[i] = arvore.readLong();
            no.ids[i] = id;
            no.adress[i] = endereco;
            
            // nao precisa colocar o ponteiro pra proximo nó pq nao tem proximo nó
            no.sort();
            writeNo(arvore, no, 8);
        }
    }

    public void createFile(RandomAccessFile arvore, int id, long endereco) throws Exception {
        no no = new no();
        no.createFile();
        no.ids[0] = id;
        no.adress[0] = endereco;
        no.quantidade = 1;
        arvore.writeByte(no.quantidade);
        arvore.writeBoolean(no.folha);
        for (int i = 0; i < no.ids.length; i++) {
            arvore.writeLong(no.pointers[i]);
            arvore.writeInt(no.ids[i]);
            arvore.writeLong(no.adress[i]);
        }
        arvore.writeLong(no.pointers[4]);
    }

    private void writeNo(RandomAccessFile arvore, no no, long seek) throws Exception{
        arvore.seek(seek);
        arvore.writeByte(no.quantidade);
        arvore.writeBoolean(no.folha);
        for (int i = 0; i < no.ids.length; i++) {
            arvore.writeLong(no.pointers[i]);
            arvore.writeInt(no.ids[i]);
            arvore.writeLong(no.adress[i]);
        }
        arvore.writeLong(no.pointers[4]);
    }

}
