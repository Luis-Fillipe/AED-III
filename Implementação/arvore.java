import java.io.IOException;
import java.io.RandomAccessFile;

public class arvore {
    RandomAccessFile arvore;

    public long insert(RandomAccessFile arvore,int id, long endereco, long seek, boolean split) throws IOException, Exception {
        
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
                    int j = 1;
                    boolean flag = false;
                    while (i < no.quantidade) {
                        if (no.ids[i] < id) {

                        } else {
                            if (flag == false) {
                                no.ids[i] = id;
                                no.adress[i] = endereco;
                                flag = true;
                            }
                            no2.ids[j] = no.ids[i];
                            no2.adress[j] = no.adress[i];
                        }
                        i++;
                    }
                    for (int k = 1; k < 5; k++) {
                        System.out.println(no.ids[k]);
                    }
                    //insert(arvore, no2.ids[1], no2.adress[1], seek, true);

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

}
