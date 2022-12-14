import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class app {
    public static boolean checkInput(String nome, int choice) throws Exception {
        file file = new file();
        boolean flag = true;
        int i = 0;
        if (choice == 1) { // verifico o nome
            while (flag == true && i < nome.length()) {
                char c = nome.charAt(i);
                if (((int) c >= 128 && c <= 191) || Character.isDigit(c)) {
                    flag = false;
                }
                i++;
            }

        } else if (choice == 2) { // verifico o nome de usuario
            flag = file.checkUsername(nome);
        } else if (choice == 3) { // verifico o cpf
            if (!(nome.length() == 11) || nome.matches("[0-9]+") == false) {
                flag = false;
            }
        } else if (choice == 4) { // verifico o nome da cidade
            if (nome.matches("^[a-zA-Z ]*$") == false) {
                flag = false;
            }
        } else if (choice == 5) { // verifico a senha
            if (nome.length() <= 3) {
                flag = false;
            }
        }

        return (flag);
    }

    public static usuario create() throws Exception {
        Scanner sc = new Scanner(System.in);
        usuario user = new usuario();
        file file = new file();
        String cpf = "";
        String nome = "";
        int choice = 1;
        user.setID();

        System.out.println("Digite seu nome: ");
        nome = sc.nextLine();
        boolean check = checkInput(nome, 1);
        while (check == false) {
            System.out.println("O nome digitado não segue os padrões, por favor digite novamente!");
            nome = sc.nextLine();
            check = checkInput(nome, 1);
        }
        user.setNome(nome);
        System.out.println("Digite seu nome de usuário: ");
        nome = sc.nextLine();
        check = checkInput(nome, 2);
        if (check == false) {
            System.out.println("Nome de usuário já utilizado!\nTente outro nome: ");
            nome = sc.nextLine();
            check = checkInput(nome, 2);
        }
        user.setNomeUsuario(nome);

        System.out.println("Digite uma senha forte: ");
        nome = sc.nextLine();
        check = checkInput(nome, 5);
        if (check == false) {
            System.out.println("Senha muito fraca!\nTente outra senha: ");
            nome = sc.nextLine();
            check = checkInput(nome, 5);
        }
        user.setSenha(nome);

        System.out.println("Digite seu CPF: ");
        cpf = sc.nextLine();
        check = checkInput(cpf, 3);
        while (check == false) {
            System.out.println("Seu CPF não segue os padrões, por favor digite novamente!");
            cpf = sc.nextLine();
            check = checkInput(cpf, 3);
        }
        user.setCPF(cpf);
        System.out.println("Digite seu email: ");
        user.setEmail(sc.nextLine());
        while (choice == 1) {
            System.out.println("Deseja adicionar outro email?\nDigite 1 para SIM e 0 para NÃO: ");
            choice = Integer.parseInt(sc.nextLine());
            while (choice > 1 || choice < 0) {
                System.out.println("Desculpe, não entendi. Tente novamente");
                choice = Integer.parseInt(sc.nextLine());
            }
            if (choice == 1) {
                System.out.println("Digite seu email: ");
                user.setEmail(sc.nextLine());
            }
        }

        System.out.println("Digite o valor do depósito inicial: ");
        choice = Integer.parseInt(sc.nextLine());
        while (choice <= 0) {
            System.out.println("Valor inválido, por favor digite novamente!");
            choice = Integer.parseInt(sc.nextLine());
        }
        user.setSaldo(choice);

        System.out.println("Digite sua cidade: ");
        nome = sc.nextLine();
        check = checkInput(nome, 4);
        while (check == false) {
            System.out.println("O nome de Cidade digitado não segue os padrões, por favor digite novamente!");
            nome = sc.nextLine();
            check = checkInput(nome, 4);
            check = checkInput(nome, 4);
        }
        user.setCidade(nome);
        return (user);
    }

    public static int delete() throws Exception {
        Scanner sc = new Scanner(System.in);
        usuario user = new usuario();
        file file = new file();
        lista lista = new lista();
        boolean result = false;
        System.out.println("Digite o ID que deseja deletar: ");
        int id = sc.nextInt();
        while (id <= 0) {
            System.out.println("ID inválido, por favor digite novamente!");
            id = sc.nextInt();
        }
        user = file.getObj(id);
        result = file.deleteUser(id);
        if (result == true) {
            lista.delete(user.getNome(), user.getId(), true, user.getCidade());
        }
        return (id);
    }

    public static void transfer() throws Exception {
        Scanner sc = new Scanner(System.in);
        usuario user = new usuario();
        file file = new file();
        boolean result = false;
        System.out.println("Digite o ID que ira enviar o dinheiro: ");
        int idTo = sc.nextInt();
        while (idTo <= 0) {
            System.out.println("ID inválido, por favor digite novamente!");
            idTo = sc.nextInt();
        }

        System.out.println("Digite o ID que ira receber o dinheiro: ");
        int idFrom = sc.nextInt();
        while (idFrom <= 0) {
            System.out.println("ID inválido, por favor digite novamente!");
            idFrom = sc.nextInt();
        }

        System.out.println("Digite o da transferencia: ");
        int valor = sc.nextInt();
        while (valor <= 0) {
            System.out.println("Valor inválido, por favor digite novamente!");
            valor = sc.nextInt();
        }

        result = file.transfer(idTo, idFrom, valor);
        if (result == true) {
            System.out.println("Transferencia realizada com sucesso!");
        } else {
            System.out.println("Sua transferencia não foi realizada, tente novamente mais tarde :(");
        }

    }

    public static boolean edit() throws Exception {
        Scanner sc = new Scanner(System.in);
        usuario user = new usuario();
        file file = new file();
        boolean result = false;
        String newAtributo = "";
        System.out.println("Digite o ID que deseja editar: ");
        int id = Integer.parseInt(sc.nextLine());
        if (id <= 0) {
            System.out.println("ID inválido, por favor digite novamente!");
        } else {
            System.out.println("Selecione uma operação:");
            System.out.println("1: Editar Nome");
            System.out.println("2: Editar Nome de Usuário");
            System.out.println("3: Editar Email");
            System.out.println("4: Editar Cidade");
            System.out.println("5: Editar Senha");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    System.out.println("Digite o novo nome: ");
                    newAtributo = sc.nextLine();
                    result = checkInput(newAtributo, choice);
                    while (result == false) {
                        System.out
                                .println("O nome digitado não segue os padrões, por favor digite novamente!");
                        newAtributo = sc.nextLine();
                        result = checkInput(newAtributo, choice);
                    }
                    file.editName(newAtributo, id);
                    break;
                case 2:
                    System.out.println("Digite o novo nome de usuário: ");
                    newAtributo = sc.nextLine();
                    result = checkInput(newAtributo, choice);
                    while (result == false) {
                        System.out.println(
                                "O nome de usuário digitado não segue os padrões, por favor digite novamente!");
                        newAtributo = sc.nextLine();
                        result = checkInput(newAtributo, choice);
                    }
                    file.editUsername(newAtributo, id);
                    break;
                case 3:

                    file.choiceEmail(id);
                    break;
                case 4:
                    System.out.println("Digite a nova cidade: ");
                    newAtributo = sc.nextLine();
                    result = checkInput(newAtributo, choice);
                    if (result == false) {
                        System.out
                                .println("O nome de Cidade digitado não segue os padrões, por favor digite novamente!");
                        newAtributo = sc.nextLine();
                        result = checkInput(newAtributo, choice);
                    }
                    file.editCity(newAtributo, id);
                    break;
                case 5:
                    System.out.println("Digite a nova senha: ");
                    newAtributo = sc.nextLine();
                    result = checkInput(newAtributo, choice);
                    if (result == false) {
                        System.out.println("Senha muito fraca!\nTente outro nome: ");
                        newAtributo = sc.nextLine();
                        result = checkInput(newAtributo, choice);
                    }
                    file.editPassword(newAtributo, id);
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }

        }
        return (result);
    }

    public static void operations() throws Exception {
        String os = System.getProperty("os.name");
        hash hash = new hash();
        usuario user = new usuario();
        file file = new file();
        no no = new no();
        arvore arvore = new arvore();
        ordenacao ordenacao = new ordenacao();
        lista lista = new lista();
        Scanner sc = new Scanner(System.in);
        System.out.println("Selecione uma operação:");
        System.out.println("01: Abrir conta");
        System.out.println("02: Apagar conta");
        System.out.println("03: Editar conta");
        System.out.println("04: Realizar transferência entre conta");
        System.out.println("05: Exibir um usuário");
        System.out.println("06: Exibir todos os usuários");
        System.out.println("07: Intercalação balanceada");
        System.out.println("08: Criar Árvore B+");
        System.out.println("09: Exibir um usuário");
        System.out.println("10: Pesquisar na Lista invertida");
        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1:
                user = create();
                // user.printUser();
                long ponteiro = file.insert(user);

                hash.insert(user.getId(), ponteiro);
                lista.inserir(user.getNome(), user.getId(), true, user.getCidade());
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 2:

                hash.delete(delete());

                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 3:
                edit();
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 4:
                transfer();
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 5:
                System.out.println("Digite o ID que deseja buscar: ");
                int id = sc.nextInt();
                file.readUser(id);
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 6:
                file.readUsers();
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 7:
                ordenacao.blocos();
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 8:
                RandomAccessFile arvoreR = new RandomAccessFile("bd/arvore.db", "rw");
                arvore.insert(arvoreR, 6, 1, 0, false, false);
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 9:
                System.out.println("Digite o ID que deseja buscar: ");
                id = sc.nextInt();
                ponteiro = hash.search(id);
                file.readUser(ponteiro);
                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            case 10:
                System.out.println("Escolha uma opção:");
                System.out.println("01: Buscar por nome");
                System.out.println("02: Buscar por cidade");
                System.out.println("03: Buscar por nome e cidade");
                choice = Integer.parseInt(sc.nextLine());
                String entrada;
                boolean check = false;
                ArrayList<Integer> ids;
                switch (choice) {
                    case 1:
                        System.out.println("Digite o nome a ser buscado: ");
                        entrada = sc.nextLine();
                        check = checkInput(entrada, 1);
                        while (check == false) {
                            System.out.println("O nome digitado não segue os padrões, por favor digite novamente!");
                            entrada = sc.nextLine();
                            check = checkInput(entrada, 1);
                        }
                        ids = lista.searchNome(entrada);
                        System.out.println(ids);
                        break;
                    case 2:
                        System.out.println("Digite o nome a ser buscado: ");
                        entrada = sc.nextLine();
                        check = checkInput(entrada, 1);
                        while (check == false) {
                            System.out.println("O nome digitado não segue os padrões, por favor digite novamente!");
                            entrada = sc.nextLine();
                            check = checkInput(entrada, 4);
                        }
                        ids = lista.searchCidade(entrada);
                        System.out.println(ids);
                        break;
                    case 3:
                        System.out.println("Digite o nome a ser buscado: ");
                        entrada = sc.nextLine();
                        check = checkInput(entrada, 1);
                        while (check == false) {
                            System.out.println("O nome digitado não segue os padrões, por favor digite novamente!");
                            entrada = sc.nextLine();
                            check = checkInput(entrada, 1);
                        }
                        ids = lista.searchNome(entrada);
                        System.out.println("Digite o nome da cidade a ser buscado: ");
                        entrada = sc.nextLine();
                        check = checkInput(entrada, 1);
                        while (check == false) {
                            System.out.println("O nome digitado não segue os padrões, por favor digite novamente!");
                            entrada = sc.nextLine();
                            check = checkInput(entrada, 4);
                        }
                        ArrayList<Integer> ids2 = lista.searchCidade(entrada);
                        ids.retainAll(ids2);
                        System.out.println(ids);
                        break;

                    default:
                        break;
                }

                System.out.println("Deseja realizar mais alguma operação?");
                System.out.println("Digite 1 para SIM e 0 para NÃO");
                choice = sc.nextInt();
                if (choice == 1) {
                    operations();
                } else {
                    System.out.println("Até mais :)");
                }
                break;
            default:
                System.out.println("Opção Inválida");
                break;
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Bem vindo ao Sistema!");
        operations();

    }
}