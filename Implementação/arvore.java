import java.io.IOException;
import java.io.RandomAccessFile;

public class arvore {
    RandomAccessFile arvore;

    public long insert(RandomAccessFile arvore, int id, long endereco, long seek, boolean split)
            throws IOException, Exception {

        long firstFolha = -1;
        if (arvore.length() == 0) { // aqui ainda não temos nada dentro do arq de arvore
            arvore.writeLong(8);
            createFile(arvore, id, endereco);

        } else { // aqui temos apenas uma folha no arquivo
            arvore.seek(0);
            firstFolha = arvore.readLong();
            arvore.seek(firstFolha);
            int qtd = arvore.readByte();
            boolean folha = arvore.readBoolean();

            if (folha == true) { // se for folha vamos inserir
                if (qtd < 4) {
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
                    no no = new no();
                    no no2 = new no();
                    no noRaiz = new no();
                    boolean raiz = existsRaiz(arvore);
                    if (raiz == true) { // vou subir um valor a raiz
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
                        if (id > no2.ids[1]) {
                            no2.ids[3] = id;
                            no2.adress[3] = endereco;
                            no2.sort();
                        }else{
                            no.ids[3] = id;
                            no.adress[3] = endereco;
                            no.sort();
                        }
                        //jogar o menor da direita para a raiz
                        noRaiz = getRaiz(arvore);
                        noRaiz.ids
                    } else { // não existe raiz e vamos cria-la

                    }

                }

            } else { // não é folha, entao eu preciso procurar onde inserir

                if (split == false) {
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
                        if (no.ids[i] < id) {
                            seek = no.pointers[i];
                            i = no.quantidade;
                        } else {

                        }
                        i++;
                    }
                    firstFolha = insert(arvore, id, endereco, seek, false);
                    no.pointers[i] = firstFolha;
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
        while (i < no.quantidade) {
            no.pointers[i] = arvore.readLong();
            no.ids[i] = arvore.readInt();
            i++;
        }
        return no;
    }

}
