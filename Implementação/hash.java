import java.io.IOException;
import java.io.RandomAccessFile;

public class hash {
    RandomAccessFile destino;
    RandomAccessFile diretorio;
    int profundidade = 1;

    private int formulaHash(int id, int profundidade) {
        return ((int) (id % Math.pow(2, profundidade)));
    }

    public void insert(int id, long ponteiro) throws Exception {
        destino = new RandomAccessFile("bd/destino.db", "rw");
        diretorio = new RandomAccessFile("bd/diretorio.db", "rw");
        int profundidade = 1;
        
        if (diretorio.length() == 0) {
            createFiles(diretorio, destino);
        } else {
            profundidade = diretorio.readByte();
        }
        int posicao = formulaHash(id, profundidade);
        long bucket = searchBucket(diretorio, posicao, profundidade);
        // inserção de fato
        destino.seek(bucket);
        if (bucket != -1) {
            int profBucket = destino.readByte();
            long backupQtd = destino.getFilePointer();
            int qtd = destino.readByte();
            if (qtd < 4) {

                boolean flag = false;
                while (flag == false) {
                    if (destino.readInt() == 0) {
                        flag = insercao(destino, id, backupQtd, qtd, ponteiro);
                        destino.seek(backupQtd);
                        destino.writeByte(++qtd);
                    } else {
                        destino.skipBytes(8);
                    }
                }
            } else {
                // vamos ter que aumentar a profundidade
                if (profundidade == 1) {

                    profundidade = increaseProfundidade(diretorio, destino, profundidade, bucket);
                    remap(diretorio, destino, profundidade, bucket, id, posicao, ponteiro);
                } else {
                    if (profBucket == profundidade) {
                        profundidade = increaseProfundidade(diretorio, destino, profundidade, bucket);

                        remap(diretorio, destino, profundidade, bucket, id, posicao, ponteiro);
                    } else {
                        System.out.println("entrei foi aqui");
                        System.out.println(backupQtd);
                        destino.seek(backupQtd - 1);
                        destino.writeByte(++profBucket);
                        checkBuckets(diretorio, destino, bucket, posicao, profBucket);
                        remapBucket(diretorio, destino, profundidade, bucket, id, posicao, ponteiro);
                    }
                }
            }
        }
        destino.close();
        diretorio.close();
    }

    public void checkBuckets(RandomAccessFile diretorio, RandomAccessFile destino, long bucket, int posicao,
            int profundidade)
            throws IOException {
        diretorio.seek(0);
        int quantidade = diretorio.readByte();
        quantidade = (int) Math.pow(2, quantidade);
        for (int i = 0; i < quantidade; i++) {
            if (i == posicao) {
                diretorio.writeLong(destino.length());
                destino.seek(destino.length());
                System.out.println("escrevi profundidade em " + destino.getFilePointer());
                destino.writeByte(profundidade);
                destino.writeByte(0);
                destino.seek(destino.length() + 50 - 3);
                destino.writeByte(0);
                i = quantidade;

            } else {
                diretorio.readLong();
            }
        }

    }

    public boolean insercao(RandomAccessFile destino, int id, long backupQtd, int qtd, long ponteiro)
            throws IOException {
        destino.seek(destino.getFilePointer() - 4);
        destino.writeInt(id);
        destino.writeLong(ponteiro);
        return true;
    }

    public void createFiles(RandomAccessFile diretorio, RandomAccessFile destino) throws Exception {
        diretorio.writeByte(1);
        diretorio.writeLong(0);
        diretorio.writeLong(51);

        destino.writeByte(1);
        destino.writeByte(0);

        destino.seek(50);
        destino.writeByte(1);
        destino.writeByte(0);

        destino.seek(99);
        destino.writeByte(0);
    }

    public int increaseProfundidade(RandomAccessFile diretorio, RandomAccessFile destino, int profundidade,
            long bucket) throws Exception {
        int diferenca = (int) Math.pow(2, profundidade);
        profundidade++;
        int diferenca2 = (int) Math.pow(2, profundidade);
        RandomAccessFile stepBack = new RandomAccessFile("bd/diretorio.db", "r");
        diretorio.seek(0);
        diretorio.writeByte(profundidade);
        diretorio.seek(diretorio.length());
        stepBack.seek(1);
        for (int i = 0; i < diferenca2 - diferenca; i++) {
            long ponteiro = stepBack.readLong();
            if (ponteiro > 0) {
                --ponteiro;
            }
            if (ponteiro == bucket) {
                diretorio.writeLong(destino.length());
            } else {
                diretorio.writeLong(ponteiro);
            }

        }
        destino.seek(destino.length());
        destino.writeByte(profundidade);
        destino.writeByte(0);
        destino.seek(destino.length() + 47);
        destino.writeByte(0);
        stepBack.close();
        destino.seek(bucket);
        destino.writeByte(profundidade);
        return profundidade;
    }

    public long searchBucket(RandomAccessFile diretorio, int posicao, int profundidade) throws Exception {
        diretorio.seek(0);
        long bucket = -1;
        profundidade = (int) Math.pow(2, profundidade);
        if (posicao <= profundidade) {
            diretorio.seek(8 * posicao + 1);
            bucket = diretorio.readLong();
        }
        if (bucket > 0) {
            bucket -= 1;
        }
        return (bucket);
    }

    public void remap(RandomAccessFile diretorio, RandomAccessFile destino, int profundidade, long bucket, int id,
            int oldPosition, long ponteiro)
            throws Exception {

        destino.seek(bucket + 1);
        long backupQtd = destino.getFilePointer();
        long backup = destino.getFilePointer();
        int qtd = destino.readByte();
        int i = 0;
        int retirado = 0;
        int quantidade = qtd;
        while (i < qtd) {
            long backupPosition = destino.getFilePointer();
            int id1 = destino.readInt();
            long endereco = destino.readLong();
            int posicao = formulaHash(id1, profundidade);

            if (posicao != oldPosition) {
                long newBucket = searchBucket(diretorio, posicao, profundidade);

                retirado++;
                destino.seek(newBucket + 2);
                backupQtd = destino.getFilePointer();
                int newQtd = destino.readByte();

                if (newQtd < 4) {
                    boolean flag = false;
                    while (flag == false) {
                        if (destino.readInt() == 0) {
                            flag = insercao(destino, id1, backupQtd, qtd, endereco);
                            destino.seek(backupQtd);

                            destino.writeByte(++newQtd);
                            delete(destino, backupPosition);
                        } else {
                            destino.skipBytes(8);
                        }
                    }
                } else {
                    System.out.println("Deu ruim");
                }

            }
            i++;
        }
        // atualizar a qtd do bucket remapeado
        destino.seek(backup);
        System.out.println(destino.getFilePointer());
        destino.writeByte(qtd - retirado);
        // adiciona o id que causou o problema todo
        int posicao = formulaHash(id, profundidade);
        long newBucket = searchBucket(diretorio, posicao, profundidade);
        destino.seek(newBucket + 1);
        backupQtd = destino.getFilePointer();
        qtd = destino.readByte();
        System.out.println("qtd " + qtd);
        if (qtd < 4) {
            boolean flag = false;
            while (flag == false) {
                if (destino.readInt() == 0) {
                    flag = insercao(destino, id, backupQtd, qtd, ponteiro);
                    destino.seek(backupQtd);
                    destino.writeByte(++qtd);
                } else {
                    destino.skipBytes(8);
                }
            }
        }
        destino.seek(backupQtd);
        destino.writeByte(qtd);
    }

    public void remapBucket(RandomAccessFile diretorio, RandomAccessFile destino, int profundidade, long bucket, int id,
            int oldPosition, long ponteiro)
            throws Exception {

        destino.seek(bucket + 1);

        long backupQtd = destino.getFilePointer();
        long backup = destino.getFilePointer();

        int qtd = destino.readByte();
        int i = 0;
        int retirado = 0;
        int quantidade = qtd;
        while (i < qtd) {
            long backupPosition = destino.getFilePointer();
            int id1 = destino.readInt();
            long endereco = destino.readLong();
            int posicao = formulaHash(id1, profundidade);
            if (posicao == oldPosition) {
                long newBucket = searchBucket(diretorio, posicao, profundidade);

                retirado++;
                destino.seek(newBucket + 2);
                backupQtd = destino.getFilePointer();
                int newQtd = destino.readByte();

                if (newQtd < 4) {
                    boolean flag = false;
                    while (flag == false) {
                        if (destino.readInt() == 0) {
                            flag = insercao(destino, id1, backupQtd, qtd, endereco);
                            destino.seek(backupQtd);

                            destino.writeByte(++newQtd);
                            delete(destino, backupPosition);
                        } else {
                            destino.skipBytes(8);
                        }
                    }
                } else {

                }

            }
            i++;
        }

        // atualizar a qtd do bucket remapeado
        destino.seek(backup);
        destino.writeByte(qtd - retirado);
        // adiciona o id que causou o problema todo

        int posicao = formulaHash(id, profundidade);
        long newBucket = searchBucket(diretorio, posicao, profundidade);
        destino.seek(newBucket + 1);
        backupQtd = destino.getFilePointer();
        qtd = destino.readByte();
        if (qtd < 4) {
            boolean flag = false;
            while (flag == false) {
                if (destino.readInt() == 0) {
                    flag = insercao(destino, id, backupQtd, qtd, ponteiro);
                    destino.seek(++backupQtd);
                    destino.writeByte(++qtd);
                } else {
                    destino.skipBytes(8);
                }
            }
        }

    }

    private void delete(RandomAccessFile destino, long backupPosition) throws IOException {
        destino.seek(backupPosition);
        destino.writeInt(0);
        destino.writeLong(0);
    }

    public void delete(int id) throws Exception {
        destino = new RandomAccessFile("bd/destino.db", "rw");
        diretorio = new RandomAccessFile("bd/diretorio.db", "rw");
        diretorio.seek(0);
        int profundidade = diretorio.readByte();
        int posicao = formulaHash(id, profundidade);
        long bucket = searchBucket(diretorio, posicao, profundidade);
        destino.seek(bucket);
        long backupQtd = destino.getFilePointer();
        destino.skipBytes(1);
        boolean flag = false;
        int qtd = destino.readByte();
        int i = 0;
        while (i < qtd) {
            long backupId = destino.getFilePointer();
            int idLido = destino.readInt();
            if (idLido == id) {
                destino.writeLong(0);
                destino.seek(backupId);
                destino.writeInt(0);
                destino.seek(backupQtd);
                i = qtd;
                destino.writeByte(--qtd);
                flag = true;

            } else {
                destino.skipBytes(8);

            }
            i++;
        }
        if (flag == false) {
            System.out.println("ID nao existe!");
        }
        destino.close();
        diretorio.close();
    }

    public long search(int id) throws Exception {
        destino = new RandomAccessFile("bd/destino.db", "r");
        diretorio = new RandomAccessFile("bd/diretorio.db", "r");
        diretorio.seek(0);
        int profundidade = diretorio.readByte();

        int posicao = formulaHash(id, profundidade);
        long bucket = searchBucket(diretorio, posicao, profundidade);

        if (posicao == 3) {
            bucket++;
        }
        destino.seek(bucket);
        destino.readByte();
        boolean flag = false;
        int qtd = destino.readByte();

        long ponteiro = -1;
        int i = 0;
        while (i < 4) {
            int idLido = destino.readInt();

            if (idLido == id) {

                ponteiro = destino.readLong();
                i = qtd;
                flag = true;

            } else {
                destino.skipBytes(8);

            }
            i++;
        }
        if (flag == false) {
            System.out.println("ID nao existe!");
        }
        destino.close();
        diretorio.close();
        return ponteiro;
    }

    public void edit(int id, long adress) throws Exception {
        destino = new RandomAccessFile("bd/destino.db", "rw");
        diretorio = new RandomAccessFile("bd/diretorio.db", "rw");
        diretorio.seek(0);
        int profundidade = diretorio.readByte();
        int posicao = formulaHash(id, profundidade);
        long bucket = searchBucket(diretorio, posicao, profundidade);
        destino.seek(bucket);
        destino.skipBytes(1);
        boolean flag = false;
        int qtd = destino.readByte();
        long ponteiro = -1;
        int i = 0;
        while (i < qtd) {
            if (destino.readInt() == id) {
                destino.writeLong(adress);
                i = qtd;
                flag = true;

            } else {
                destino.skipBytes(8);

            }
            i++;
        }
        if (flag == false) {
            System.out.println("ID nao existe!");
        }
        destino.close();
        diretorio.close();
    }

}
