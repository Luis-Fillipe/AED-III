import java.io.IOException;
import java.io.RandomAccessFile;

public class arvore {
    RandomAccessFile arvore;

    public long insert(RandomAccessFile arvore, int id, long endereco, long seek, boolean split, boolean recursivo)
            throws IOException, Exception {

        long firstFolha = -1;
        System.out.println("seek " + seek);
        if (arvore.length() == 0) { // aqui ainda não temos nada dentro do arq de arvore
            arvore.writeLong(8);
            createFile(arvore, id, endereco);

        } else { // aqui temos apenas uma folha no arquivo
            arvore.seek(seek);
            if (recursivo == false) {
                firstFolha = arvore.readLong();
                arvore.seek(firstFolha);
            }

            int qtd = arvore.readByte();
            boolean folha = arvore.readBoolean();
            System.out.println(folha);
            if (folha == true) { // se for folha vamos inserir
                System.out.println("QTD = " + qtd);
                if (qtd < 4) {
                    System.out.println("Entrei aqui");
                    no no = new no();
                    no.createFile();
                    no.quantidade = qtd;
                    no.folha = true;
                    int i = 1;
                    qtd += 1;
                    while (i < qtd) {

                        no.pointers[i] = arvore.readLong();
                        no.ids[i] = arvore.readInt();
                        no.adress[i] = arvore.readLong();
                        i++;
                    }

                    no.quantidade = i;
                    no.pointers[i] = arvore.readLong();
                    no.ids[i] = id;
                    no.adress[i] = endereco;

                    // nao precisa colocar o ponteiro pra proximo nó pq nao tem proximo nó
                    no.sort();
                    writeNo(arvore, no, 8);
                } else { // nao cabe vou ter que dar split
                    System.out.println("entrei erroneamente aqui");
                    no no = new no();
                    no.createFile();
                    no no2 = new no();
                    no2.createFile();
                    no noRaiz = new no();
                    noRaiz.createFile();
                    boolean raiz = existsRaiz(arvore);
                    if (raiz == false) { // vou subir um valor a raiz
                        int i = 1;

                        while (i < 5) {
                            if (i < 3) {
                                no.pointers[i] = arvore.readLong();
                                no.ids[i] = arvore.readInt();
                                no.adress[i] = arvore.readLong();
                            } else {
                                no2.pointers[i] = arvore.readLong();
                                no2.ids[i] = arvore.readInt();
                                no2.adress[i] = arvore.readLong();
                            }
                            i++;
                        }
                        writeNo(arvore, no, firstFolha);

                        no2.ids[3] = id;
                        no2.adress[3] = endereco;
                        no2.quantidade++;
                        no2.sort();
                        long backup = arvore.length();
                        writeNo(arvore, no2, arvore.length());
                        // jogar o menor da direita para a raiz
                        noRaiz = getRaiz(arvore);
                        noRaiz.ids[noRaiz.quantidade] = no2.ids[1];
                        noRaiz.adress[noRaiz.quantidade] = backup;
                    } else { // não existe raiz e vamos cria-la

                        int i = 1;
                        int j = 1;
                        while (i < 5) {
                            if (i < 3) {
                                no.pointers[i] = arvore.readLong();
                                no.ids[i] = arvore.readInt();
                                no.adress[i] = arvore.readLong();
                            } else {
                                no2.pointers[j] = arvore.readLong();
                                no2.ids[j] = arvore.readInt();
                                no2.adress[j] = arvore.readLong();
                                j++;
                            }
                            i++;
                        }
                        no.quantidade = 2;
                        no2.ids[3] = id;
                        no2.adress[3] = endereco;
                        no2.quantidade++;
                        System.out.println("ENTREI ONDE DEVERIA");

                        for (j = 1; j < 5; j++) {
                            System.out.println(no2.ids[j]);
                        }

                        // jogar o menor da direita para a raiz
                        noRaiz.ids[1] = no2.ids[1];
                        arvore.setLength(0);
                        arvore.writeLong(8);
                        noRaiz.quantidade = 1;
                        noRaiz.folha = false;
                        writeNoRaiz(arvore, noRaiz, arvore.length());
                        long no1 = arvore.length();
                        writeNo(arvore, no, no1);
                        long no22 = arvore.length();
                        writeNo(arvore, no2, no22);
                        arvore.seek(10);
                        arvore.writeLong(no1);
                        arvore.seek(22);
                        arvore.writeLong(no22);
                    }

                }

            } else { // não é folha, entao eu preciso procurar onde inserir

                if (split == false) {
                    int i = 0;
                    System.out.println("qtd " + qtd);
                    while (i < qtd) {
                        long ponteiro = arvore.readLong();
                        int idLido = arvore.readInt();
                        long ponteiro2 = arvore.readLong();
                        i++;
                        System.out.println(idLido);
                        if (id < idLido) {
                            seek = ponteiro;
                        } else {
                            if (i == qtd) {
                                seek = ponteiro2;
                            }
                        }

                    }
                    System.out.println(seek);

                    firstFolha = insert(arvore, id, endereco, seek, false, true);

                } else {
                    no no = new no();
                    int i = 1;
                    while (i < qtd) {

                        no.pointers[i] = arvore.readLong();
                        no.ids[i] = arvore.readInt();
                        no.adress[i] = arvore.readLong();
                        i++;
                    }
                    no.quantidade = i;
                    no.pointers[i] = arvore.readLong();
                    i = 1;
                    while (i < no.quantidade) {
                        if (no.ids[i] == -1) {
                            no.ids[i] = id;
                            no.adress[i] = endereco;

                        } else {

                        }
                        i++;
                    }
                }

            }

        }
        return (firstFolha);

    }

    public void createFile(RandomAccessFile arvore, int id, long endereco) throws Exception {
        no no = new no();
        no.createFile();
        no.ids[1] = id;
        no.adress[1] = endereco;
        no.quantidade = 1;
        arvore.writeByte(no.quantidade);
        arvore.writeBoolean(no.folha);
        for (int i = 1; i < no.ids.length; i++) {
            arvore.writeLong(no.pointers[i]);
            arvore.writeInt(no.ids[i]);
            arvore.writeLong(no.adress[i]);
        }
        arvore.writeLong(no.pointers[5]);
    }

    private void writeNo(RandomAccessFile arvore, no no, long seek) throws Exception {
        arvore.seek(seek);
        arvore.writeByte(no.quantidade);
        arvore.writeBoolean(no.folha);
        for (int i = 1; i < no.ids.length; i++) {
            arvore.writeLong(no.pointers[i]);
            arvore.writeInt(no.ids[i]);
            arvore.writeLong(no.adress[i]);
        }
        arvore.writeLong(no.pointers[5]);
    }

    private void writeNoRaiz(RandomAccessFile arvore, no no, long seek) throws Exception {
        arvore.seek(seek);
        arvore.writeByte(no.quantidade);
        arvore.writeBoolean(no.folha);
        for (int i = 1; i < no.ids.length; i++) {
            arvore.writeLong(no.pointers[i]);
            arvore.writeInt(no.ids[i]);
        }
        arvore.writeLong(no.pointers[5]);
    }

    private boolean existsRaiz(RandomAccessFile arvore) throws Exception {
        arvore.seek(0);
        long ponteiro = arvore.readLong();
        arvore.seek(ponteiro);
        arvore.skipBytes(1);
        boolean resp = arvore.readBoolean();
        return resp;
    }

    private no getRaiz(RandomAccessFile arvore) throws Exception {
        arvore.seek(0);
        no no = new no();
        long ponteiro = arvore.readLong();
        arvore.seek(ponteiro);
        no.quantidade = arvore.readByte();
        no.folha = arvore.readBoolean();
        int i = 1;
        while (i <= no.quantidade) {
            no.pointers[i] = arvore.readLong();
            no.ids[i] = arvore.readInt();
            i++;
        }
        return no;
    }

}
