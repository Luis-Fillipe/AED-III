import java.io.RandomAccessFile;
public class hash {
    RandomAccessFile destino;
    RandomAccessFile diretorio;
    int profundidade = 1;

    private int formulaHash(int id, int profundidade) {
        return ((int) (id % Math.pow(2, this.profundidade)));
    }

    public void insert(int id) throws Exception {
        destino = new RandomAccessFile("bd/destino.db", "rw");
        diretorio = new RandomAccessFile("bd/diretorio.db", "rw");
        int profundidade = 1;
        if (diretorio.length() == 0) {
            createFiles(diretorio, destino);
        } else {
            profundidade = diretorio.readByte();
        }
        int posicao = formulaHash(id, profundidade);
        long ponteiro = searchBucket(diretorio, posicao);
        // inserção de fato
        destino.seek(ponteiro);
        if (ponteiro != -1) {

            destino.skipBytes(1);
            long backupQtd = destino.getFilePointer();
            int qtd = destino.readByte();
            if (qtd < 4) {
                if (destino.length() == destino.getFilePointer()) {
                    destino.writeInt(id);
                    destino.writeLong(5);
                    destino.seek(backupQtd);
                    destino.writeByte(++qtd);
                } else {
                    boolean flag = false;
                    while (flag == false) {
                        if (destino.readInt() == 0) {
                            destino.writeInt(id);
                            destino.writeLong(5);
                            destino.seek(backupQtd);
                            destino.writeByte(++qtd);
                            flag = true;
                        } else {
                            destino.skipBytes(8);
                        }
                    }
                }
            } else{

            }
        }
    }

    public void createFiles(RandomAccessFile diretorio, RandomAccessFile destino) throws Exception {
        diretorio.writeByte(1);
        diretorio.writeLong(0);
        diretorio.writeLong(51);

        destino.writeByte(1);
        destino.writeByte(0);
        
        destino.seek(50);
        destino.writeByte(5);
        destino.writeByte(0);
        
        destino.seek(104);
        destino.writeByte(2);
        destino.writeByte(0);
    }

    public long searchBucket(RandomAccessFile diretorio, int posicao) throws Exception {
        diretorio.seek(0);
        int profundidade = diretorio.readByte();
        long bucket = -1;
        if (posicao <= profundidade) {
            int i = 0;
            while (i <= posicao) {
                if (i == posicao) {
                    bucket = diretorio.readLong();
                } else {
                    diretorio.skipBytes(8);
                }
                i++;
            }

        }
        return (bucket);
    }

    /*
     * Pseudocodigo
     * Iremos realizar a criação de um arquivo de indice por ID
     * Este arquivo de indice tera ate 4 registros em cada bucket
     * Poderemos fazer buscas usando nosso arquivo de Hash
     */

    /*
     * A ideia é
     * quando houver a necessidade de aumentar a profundidade do arquivo, vamos ter
     * que criar um novo arq e realocar tudo
     */
}
