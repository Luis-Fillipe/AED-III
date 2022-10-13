public class no {
    int ids[] = new int[4];
    long adress[] = new long[4];
    long pointers[] = new long[5];
    boolean folha = true;
    int quantidade = 0;

    public void createFile() {
        for (int i = 0; i < 4; i++) {
            ids[i] = -1;
            adress[i] = -1;
            pointers[i] = -1;
        }
        pointers[4] = -1;
    }

    public void sort() {

        int n = quantidade + 1;
        for (int i = 1; i < n; ++i) {
            System.out.println("index = " + i);
            if (ids[i - 1] == -1) {
                i = n;
            } else {
                int chave = ids[i - 1];
                long endereco = adress[i - 1];
                long ponteiro = pointers[i];
                int j = i - 1;

                while (j >= 0 && ids[j] > chave) {
                    System.out.println("ids[" + j + "] = " + ids[j] + " é > que chave = " + chave);
                    ids[j + 1] = ids[j];
                    pointers[j + 1] = pointers[j];
                    adress[j + 1] = adress[j];
                    j = j - 1;
                }
                System.out.println("j = " + j);
                if (j + 1 == 4) {
                    ids[j] = chave;
                    pointers[j] = ponteiro;
                    adress[j] = endereco;
                } else {
                    ids[j + 1] = chave;
                    pointers[j + 1] = ponteiro;
                    adress[j + 1] = endereco;
                }

            }

        }

    }

}
// Um nó folha gasta 90 bytes no arquivo
// A raiz gasta 58 bytes
