package visao;

import entidades.Curso;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// CLASSE DE VISÃO PARA CURSO
// Responsável por toda interação com o usuário via terminal para a entidade Curso:
// - Exibição de menus e listas de cursos
// - Leitura de dados para cadastro e alteração
// - Confirmações e mensagens
// Esta classe NÃO contém lógica de negócio. Ela apenas coleta e exibe informações.
public class VisaoCurso {

    private Scanner console;

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    // Construtor recebe o Scanner (normalmente System.in) para leitura do teclado
    public VisaoCurso(Scanner console) {
        this.console = console;
        if (DEBUG) System.out.println("[DEBUG] VisaoCurso inicializada.");
    }

    // ------------------------------------------------------------------------------
    // MENUS E LISTAGENS
    // ------------------------------------------------------------------------------

    // Exibe o menu da seção "Meus cursos" e retorna a opção escolhida.
    // Pode ser: "A" (Novo curso), "R" (Retornar) ou um número (índice do curso).
    public String menuCursos() {
        if (DEBUG) System.out.println("[DEBUG] Exibindo menu de cursos.");
        System.out.println("\n(A) Novo curso");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção (ou número do curso para detalhes): ");
        String opcao = this.console.nextLine().trim().toUpperCase();
        if (DEBUG) System.out.println("[DEBUG] Opção escolhida: " + opcao);
        return opcao;
    }

    // Exibe a lista numerada de cursos do usuário (ordenada alfabeticamente pelo controle).
    // Mostra também o breadcrumb da localização atual.
    public void listarCursos(ArrayList<Curso> cursos) {
        System.out.println("\n\nEntrePares 1.0");
        System.out.println("--------------");
        System.out.println("> Início > Meus cursos\n");

        if (cursos != null && !cursos.isEmpty()) {
            for (int i = 0; i < cursos.size(); i++) {
                Curso c = cursos.get(i);
                System.out.printf("%d - %s\n", i + 1, c.getNome());
            }
            if (DEBUG) System.out.println("[DEBUG] Listados " + cursos.size() + " cursos.");
        } else {
            System.out.println("Nenhum curso cadastrado.");
            if (DEBUG) System.out.println("[DEBUG] Lista de cursos vazia.");
        }
    }

    // ------------------------------------------------------------------------------
    // LEITURA DE DADOS (CADASTRO E ALTERAÇÃO)
    // ------------------------------------------------------------------------------

    // Lê os dados para criação de um novo curso.
    // Recebe o ID do usuário logado e o código compartilhável (já gerado pelo controle).
    // Retorna um objeto Curso preenchido (sem ID, que será gerado pelo ArquivoCurso).
    public Curso lerNovoCurso(int idUsuario, String codigo) {
        if (DEBUG) System.out.println("[DEBUG] Iniciando leitura de novo curso para usuário ID: " + idUsuario);
        System.out.println("\n--- NOVO CURSO ---");
        System.out.print("Nome do curso: ");
        String nome = this.console.nextLine().trim();

        System.out.print("Descrição detalhada: ");
        String descricao = this.console.nextLine().trim();

        LocalDate dataInicio = null;
        while (dataInicio == null) {
            System.out.print("Data de início (DD/MM/AAAA): ");
            String entrada = this.console.nextLine().trim();
            try {
                dataInicio = LocalDate.parse(entrada, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato DD/MM/AAAA.");
                if (DEBUG) System.out.println("[DEBUG] Erro ao parsear data: " + entrada);
            }
        }

        if (DEBUG) System.out.println("[DEBUG] Dados lidos: " + nome + ", código: " + codigo);
        // Estado inicial sempre 0 (ativo e recebendo inscrições)
        return new Curso(-1, nome, descricao, dataInicio, codigo, (byte) 0, idUsuario);
    }

    // Lê as alterações para um curso existente. Campos em branco mantêm o valor atual.
    // Retorna um objeto Curso com os campos atualizados (mantendo ID, código e estado).
    public Curso lerAlteracoes(Curso cursoAtual) {
        if (DEBUG) System.out.println("[DEBUG] Iniciando leitura de alterações para curso ID: " + cursoAtual.getID());
        System.out.println("\n--- ALTERAR CURSO ---");
        System.out.println("Deixe o campo em branco para manter o valor atual.");

        System.out.print("Novo nome [" + cursoAtual.getNome() + "]: ");
        String nome = this.console.nextLine().trim();
        if (nome.isEmpty()) nome = cursoAtual.getNome();

        System.out.print("Nova descrição [" + cursoAtual.getDescricao() + "]: ");
        String descricao = this.console.nextLine().trim();
        if (descricao.isEmpty()) descricao = cursoAtual.getDescricao();

        LocalDate dataInicio = null;
        while (dataInicio == null) {
            System.out.print("Nova data de início (DD/MM/AAAA) [" +
                    cursoAtual.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "]: ");
            String entrada = this.console.nextLine().trim();
            if (entrada.isEmpty()) {
                dataInicio = cursoAtual.getDataInicio();
            } else {
                try {
                    dataInicio = LocalDate.parse(entrada, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (DateTimeParseException e) {
                    System.out.println("Data inválida. Use o formato DD/MM/AAAA.");
                }
            }
        }

        if (DEBUG) System.out.println("[DEBUG] Alterações: nome=" + nome + ", descricao=" + descricao);
        // Cria cópia com os dados atualizados
        return new Curso(cursoAtual.getID(), nome, descricao, dataInicio,
                         cursoAtual.getCodigo(), cursoAtual.getEstado(), cursoAtual.getUsuarioId());
    }

    // ------------------------------------------------------------------------------
    // EXIBIÇÃO DE DETALHES DO CURSO
    // ------------------------------------------------------------------------------

    // Exibe os dados completos de um curso, incluindo estado por extenso.
    public void mostrarCursoDetalhado(Curso curso) {
        System.out.println("\n-------------------------------------------");
        System.out.println("> Início > Meus cursos > " + curso.getNome());
        System.out.println("\nCÓDIGO......: " + curso.getCodigo());
        System.out.println("NOME........: " + curso.getNome());
        System.out.println("DESCRIÇÃO...: " + curso.getDescricao());
        System.out.println("DATA INÍCIO.: " + curso.getDataInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        String[] estados = {"Ativo (Aberto)", "Inscrições Encerradas", "Concluído", "Cancelado"};
        String estadoStr = estados[curso.getEstado()];
        System.out.println("ESTADO......: " + estadoStr);

        if (DEBUG) System.out.println("[DEBUG] Detalhes do curso ID: " + curso.getID() + " exibidos.");
    }

    // Exibe o menu de ações disponíveis para o curso selecionado.
    public String menuDetalhesCurso() {
        if (DEBUG) System.out.println("[DEBUG] Exibindo menu de detalhes do curso.");
        System.out.println("\n(A) Gerenciar inscritos (TP2)");
        System.out.println("(B) Corrigir dados do curso");
        System.out.println("(C) Encerrar inscrições");
        System.out.println("(D) Concluir curso");
        System.out.println("(E) Cancelar/Excluir curso");
        System.out.println("(R) Retornar");
        System.out.print("\nOpção: ");
        String opcao = this.console.nextLine().trim().toUpperCase();
        if (DEBUG) System.out.println("[DEBUG] Opção escolhida: " + opcao);
        return opcao;
    }

    // ------------------------------------------------------------------------------
    // CONFIRMAÇÃO E MENSAGENS
    // ------------------------------------------------------------------------------

    // Exibe uma pergunta de confirmação e retorna true se o usuário responder 'S'.
    public boolean confirmar(String mensagem) {
        System.out.print(mensagem + " (S/N) ? ");
        String resposta = this.console.nextLine().trim().toUpperCase();
        boolean confirmado = resposta.length() > 0 && resposta.charAt(0) == 'S';
        if (DEBUG) System.out.println("[DEBUG] Confirmação: " + mensagem + " -> " + confirmado);
        return confirmado;
    }

    // Exibe uma mensagem simples na tela.
    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
        if (DEBUG) System.out.println("[DEBUG] Mensagem exibida: " + mensagem);
    }
}