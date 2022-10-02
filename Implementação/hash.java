public class hash {

    static int formulaHash(int id, int profundidade) {
        return ((int) (id % Math.pow(2, profundidade)));
    }

    public void createHash() throws Exception {
        
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
