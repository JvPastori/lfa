import java.util.Scanner;

// Classe para a transicao
class Tran {
    int eo; // Estado de origem
    char sl; // Simbolo lido
    int ed; // Estado de destino
    char se; // Simbolo escrito
    char d; // Direcao

    public Tran() {
        this.eo = -1;
    }
}

public class Maq {
    // Define os tamanhos maximos
    static final int MAX_ESTADOS = 100;
    static final int MAX_SIMBOLOS = 100;
    static final int TAMANHO_MAXIMO_FITA = 1000;

    // Arrays estaticos para armazenar a tabela de transicoes, estados de aceitacao, alfabeto e fita de entrada
    static Tran[][] transi = new Tran[MAX_ESTADOS][MAX_SIMBOLOS]; // Tabela de transicoes
    static int[] ea = new int[MAX_ESTADOS]; // Estados de aceitacao
    static char[] alfa = new char[MAX_SIMBOLOS]; // Alfabeto
    static char[] alfaAux = new char[MAX_SIMBOLOS]; // Alfabeto auxiliar
    static char[] fita = new char[TAMANHO_MAXIMO_FITA]; // Fita de entrada
    static int estadoInicial;
    static char marcadorInicio;
    static char simboloBranco;

    // Inicializa todas as transicoes como invalidas
    static void iniciar(int ne, int ns) {
        for (int i = 0; i < ne; i++) {
            for (int j = 0; j < ns; j++) {
                transi[i][j] = new Tran();
            }
        }
    }

    // Retorna o indice de um simbolo no alfabeto
    static int indice(char s, int ns) {
        for (int i = 0; i < ns; i++) {
            if (alfa[i] == s || alfaAux[i] == s || s == marcadorInicio || s == simboloBranco) {
                return i;
            }
        }
        return -1;
    }

    // Define a tabela de transicoes com base na entrada do usuario
    static void tabela(Scanner sc, int ne, int ns) {
        System.out.println("===== TABELA DE TRANSICAO =====");
        System.out.println("Digite as transicoes:");
        System.out.println("Obs 1: Caso nao haja transicoes, insira x");
        System.out.println("Obs 2: Qualquer transicao invalida fara com que o campo seja anulado.");

        for (int i = 0; i < ne; i++) {
            for (int j = 0; j < ns; j++) {
                System.out.println("-----------------------------------");
                System.out.printf("Digite o estado futuro da transicao: S%d,%d%n", i + 1, j + 1);
                String input = sc.next();
                if (input.equalsIgnoreCase("x")) {
                    System.out.println("O campo sera anulado!");
                    transi[i][j] = new Tran(); // Anula a transicao
                    continue;
                }
                int ed = Integer.parseInt(input);
                if (ed >= ne || ed < 0) {
                    System.out.println("Estado futuro invalido! O campo sera anulado.");
                    transi[i][j] = new Tran(); // Anula a transicao
                    continue;
                }

                System.out.printf("Digite o alfabeto futuro da transicao: S%d,%d%n", i + 1, j + 1);
                char se = sc.next().charAt(0);

                System.out.printf("Digite a direcao da transicao: S%d,%d (D para Direita ou E para Esquerda)%n", i + 1, j + 1);
                char d = sc.next().charAt(0);

                transi[i][j] = new Tran();
                transi[i][j].eo = i;
                transi[i][j].sl = alfa[j]; // Simbolo lido
                transi[i][j].ed = ed;
                transi[i][j].se = se;
                transi[i][j].d = d;
            }
        }
    }

    // Verifica se um estado e um estado de aceitacao
    static boolean ehFinal(int e, int nea) {
        for (int i = 0; i < nea; i++) {
            if (ea[i] == e) {
                return true;
            }
        }
        return false;
    }

    // Exibe a fita de forma legivel
    static void exibirFita(int pFita) {
        System.out.print("Fita final\n>");
        int start = Math.max(0, pFita - 10); // Ajusta o inicio para mostrar a fita em torno da posicao atual
        int end = Math.min(fita.length, pFita + 10); // Ajusta o fim para mostrar a fita em torno da posicao atual
        for (int i = start; i < end; i++) {
            System.out.print(fita[i]);
        }
        System.out.println("<");
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    // Executa a Maquina de Turing com base na fita de entrada e nas transicoes definidas
    static boolean exec(Scanner sc, int ns, int nea) {
        int eAtual = estadoInicial;  // Estado inicial
        int pFita = TAMANHO_MAXIMO_FITA / 2;  // Posicao inicial da cabeca de leitura/escrita no meio da fita

        System.out.print("Entre com a palavra a ser testada: ");
        String palavra = sc.next();  // Le a palavra de entrada
        int palavraLength = palavra.length();
        int fitaStart = pFita - palavraLength / 2;

        // Limpa a fita e copia a palavra para o centro da fita
        for (int i = 0; i < fita.length; i++) {
            fita[i] = simboloBranco;
        }
        System.arraycopy(palavra.toCharArray(), 0, fita, fitaStart, palavraLength);  // Copia a palavra para a fita

        while (true) {
            int is = indice(fita[pFita], ns);  // Obtem o indice do simbolo na fita

            if (is == -1) {  // Verifica se o simbolo e valido
                System.out.println("Simbolo invalido na fita!");
                return false;
            }

            Tran t = transi[eAtual][is];  // Obtem a transicao correspondente

            if (t.eo == -1) {  // Verifica se a transicao e valida
                exibirFita(pFita);
                System.out.println("Palavra nao aceita!");
                return false;
            }

            fita[pFita] = t.se;  // Escreve o simbolo na fita
            eAtual = t.ed;  // Atualiza o estado atual

            if (t.d == 'D') {  // Move a cabeca de leitura/escrita para a direita
                pFita++;
            } else {  // Move a cabeca de leitura/escrita para a esquerda
                pFita--;
            }

            if (ehFinal(eAtual, nea)) {  // Verifica se o estado atual e um estado de aceitacao
                exibirFita(pFita);
                System.out.println("A palavra foi aceita.");
                return true;
            }
        }
    }

    // Metodo principal que gerencia a entrada do usuario e a execucao da Maquina de Turing
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("------Linguagens Formais, Automatos e Computabilidade------");
        System.out.println("* Professor Guilherme Nakahata");
        System.out.println("--------Maquina de Turing--------");

        // Le a quantidade de simbolos do alfabeto
        System.out.print("Informe a quantidade de letras do alfabeto: ");
        int qAlfa = sc.nextInt();

        // Le os simbolos do alfabeto
        for (int i = 0; i < qAlfa; i++) {
            System.out.print("Informe uma letra do alfabeto: ");
            alfa[i] = sc.next().charAt(0);
        }

        System.out.print("Informe a quantidade de letras do alfabeto auxiliar: ");
        int qAlfaAux = sc.nextInt();

        // Le os simbolos do alfabeto auxiliar
        for (int i = 0; i < qAlfaAux; i++) {
            System.out.print("Informe uma letra do alfabeto auxiliar: ");
            alfaAux[i] = sc.next().charAt(0);
        }

        System.out.print("Informe a quantidade de estados: ");
        int ne = sc.nextInt();

        System.out.print("Informe o estado inicial: ");
        estadoInicial = sc.nextInt();

        System.out.print("Informe a quantidade de estados finais: ");
        int nea = sc.nextInt();
        for (int i = 0; i < nea; i++) {
            System.out.print("Informe um estado final: ");
            ea[i] = sc.nextInt();
        }

        System.out.print("Defina um marcador de inicio: ");
        marcadorInicio = sc.next().charAt(0);

        System.out.print("Defina um simbolo para branco: ");
        simboloBranco = sc.next().charAt(0);

        // Inicializa a tabela de transicoes e define as transicoes com base na entrada do usuario
        iniciar(ne, qAlfa + qAlfaAux + 2); // Inclui o marcador de inicio e o simbolo branco
        tabela(sc, ne, qAlfa + qAlfaAux + 2);

        // Loop para permitir testar multiplas palavras
        while (true) {
            // Executa a Maquina de Turing
            exec(sc, qAlfa + qAlfaAux + 2, nea);

            // Pergunta ao usuario se deseja continuar
            System.out.print("Digite 1 para adicionar outra palavra ou 2 para sair: ");
            int opcao = sc.nextInt();
            if (opcao == 2) {
                break;
            }
        }

        sc.close();
    }
}
