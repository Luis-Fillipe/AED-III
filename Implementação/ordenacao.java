import java.io.RandomAccessFile;

public class ordenacao {
    RandomAccessFile arq;


    /*
        balanceada() - Utilizaremos 3 registros e 2 caminhos para realizar a intercalação balanceada
     */
    public void balanceada() throws Exception {
        arq = new RandomAccessFile("bd/bd.db", "r");
        
    }
    /*
     * 
     * Pseudocodigo
     * percorrer todo o arquivo testando se o registro é lapide
     * se nao for lapide pega o regisgtro e chama uma função pra transformar em objeto
     * se for lapide de um skip bytes
     * 
     * depois de pegar 3 registros em memoria principal, ordenamos e jogamos esses registros para o arquivo temp fazendo dele 1 bloco
     * repetimos o processo ate preencher todos os arquivos
     */
}
