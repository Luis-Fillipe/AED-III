public class no {
    int ids[] = new int[5];
    long adress[] = new long[5];
    long pointers[] = new long[6];
    boolean folha = true;
    int quantidade = 0;

    public void createFile() {
        for (int i = 1; i < 5; i++) {
            ids[i] = -1;
            adress[i] = -1;
            pointers[i] = -1;
        }
        pointers[5] = -1;
    }

    public void sort() {

        int n = quantidade + 1;
        for (int i = 2; i < n; ++i) {
            System.out.println("index = " + i);

            int chave = ids[i];
            long endereco = adress[i];
            long ponteiro = pointers[i];
            int j = i - 1;

            while (j >= 1 && ids[j] > chave) {
                System.out.println("ids[" + j + "] = " + ids[j] + " é > que chave = " + chave);
                ids[j + 1] = ids[j];
                pointers[j + 1] = pointers[j];
                adress[j + 1] = adress[j];
                j = j - 1;
            }
            System.out.println("j = " + j);

            ids[j + 1] = chave;
            pointers[j + 1] = ponteiro;
            adress[j + 1] = endereco;
                    
        }

    }

}
// Um nó folha gasta 90 bytes no arquivo
// A raiz gasta 58 bytes
