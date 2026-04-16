import controle.ControleCurso;
import controle.ControleUsuario;

import java.util.Scanner;

// CLASSE PRINCIPAL DO SISTEMA ENTREPARES
// Ponto de entrada da aplicação.
// Gerencia o loop principal, a autenticação e a navegação entre os módulos.
public class Principal {

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        try {
            // Inicializa os controles (cada um instancia seus respectivos arquivos e
            // visões)
            ControleUsuario ctrlUsuario = new ControleUsuario(console);
            ControleCurso ctrlCurso = new ControleCurso(console);

            if (DEBUG)
                System.out.println("[DEBUG] Sistema iniciado. Iniciando autenticação...");

            // Tela de autenticação (login/cadastro/recuperação)
            if (!ctrlUsuario.autenticar()) {
                System.out.println("Saindo do sistema...");
                return;
            }

            // Loop do menu principal (usuário já logado)
            String opcao;
            do {
                exibirMenuPrincipal();
                opcao = console.nextLine().trim().toUpperCase();

                switch (opcao) {
                    case "A":
                        // Meus dados (perfil do usuário)
                        if (DEBUG)
                            System.out.println("[DEBUG] Acessando 'Meus dados'.");
                        ctrlUsuario.menuMeusDados();
                        break;
                    case "B":
                        // Meus cursos (gerenciamento de cursos)
                        if (DEBUG)
                            System.out.println("[DEBUG] Acessando 'Meus cursos'.");
                        ctrlCurso.gerenciarCursos();
                        break;
                    case "C":
                        // Minhas inscrições (futuro TP2)
                        System.out.println("\nFuncionalidade 'Minhas inscrições' disponível apenas no TP2.");
                        break;
                    case "P":
                        if (DEBUG)
                            System.out.println("[DEBUG] Executando população do BD.");
                        testes.PopularBD.executar();
                        break;
                    case "S":
                        // Sair
                        if (DEBUG)
                            System.out.println("[DEBUG] Usuário escolheu sair.");
                        System.out.println("Saindo do sistema...");
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } while (!opcao.equals("S"));

            // Fecha os arquivos antes de encerrar
            ctrlUsuario.close();
            ctrlCurso.close();
            if (DEBUG)
                System.out.println("[DEBUG] Sistema encerrado com sucesso.");

        } catch (Exception e) {
            System.err.println("Erro fatal na execução do sistema:");
            e.printStackTrace();
        }
    }

    // Exibe o menu principal com breadcrumb "> Início"
    private static void exibirMenuPrincipal() {
        System.out.println("\n\nEntrePares 1.0");
        System.out.println("--------------");
        System.out.println("\n> Início\n");
        System.out.println("(A) Meus dados");
        System.out.println("(B) Meus cursos");
        System.out.println("(C) Minhas inscrições");
        System.out.println("(P) Popular BD (teste)");
        System.out.println("(S) Sair");
        System.out.print("\nOpção: ");
    }
}
