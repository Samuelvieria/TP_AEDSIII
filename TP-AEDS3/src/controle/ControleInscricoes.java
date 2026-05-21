package controle;

import arquivos.ArquivoCurso;
import arquivos.ArquivoInscricao;
import entidades.Curso;
import entidades.Inscricao; // Modificado para a entidade real do seu grupo
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class ControleInscricoes {

    private ArquivoCurso arqCurso;
    private ArquivoInscricao arqInscricao;
    private Scanner console;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ControleInscricoes(Scanner console) throws Exception {
        this.console = console;
        this.arqCurso = new ArquivoCurso();
        this.arqInscricao = new ArquivoInscricao();
    }

    public void menuPrincipalInscricoes() {
        if (!Sessao.isLogado()) {
            System.out.println("Acesso negado. Usuário precisa estar logado.");
            return;
        }

        String opcao = "";
        while (!opcao.equals("R")) {
            System.out.println("\nEntrePares 1.0 --------------");
            System.out.println("> Início > Minhas inscrições");
            System.out.println("\nINSCRIÇÕES");

            try {
                int idUsuarioLogado = Sessao.getIdUsuarioLogado();
                // CORREÇÃO: Tipo alterado de CursoUsuario para Inscricao
                ArrayList<Inscricao> minhasMatriculas = arqInscricao.listarPorUsuario(idUsuarioLogado);
                ArrayList<Curso> cursosInscritos = new ArrayList<>();

                int contador = 1;
                for (Inscricao relacao : minhasMatriculas) {
                    Curso c = arqCurso.read(relacao.getIdCurso());
                    if (c != null) {
                        cursosInscritos.add(c);
                        String tagEstado = "";
                        if (c.getEstado() == 1)
                            tagEstado = " (INSCRIÇÕES ENCERRADAS)";
                        else if (c.getEstado() == 2)
                            tagEstado = " (CURSO CONCLUÍDO)";
                        else if (c.getEstado() == 3)
                            tagEstado = " (CURSO CANCELADO)";

                        System.out.println("(" + contador + ") " + c.getNome() + " - " + c.getDataInicio().format(dtf)
                                + tagEstado);
                        contador++;
                    }
                }

                if (minhasMatriculas.isEmpty()) {
                    System.out.println("[Nenhuma inscrição ativa encontrada]");
                }

                System.out.println("\n(A) Buscar curso por código");
                System.out.println("(B) Buscar curso por palavras-chave (Disponível no TP3)");
                System.out.println("(C) Listar todos os cursos");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("\nOpção: ");
                opcao = console.nextLine().trim().toUpperCase();

                if (opcao.matches("\\d+")) {
                    int idx = Integer.parseInt(opcao) - 1;
                    if (idx >= 0 && idx < cursosInscritos.size()) {
                        exibirDetalhesCurso(cursosInscritos.get(idx), true);
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } else {
                    switch (opcao) {
                        case "A":
                            buscarCursoPorCodigo();
                            break;
                        case "B":
                            System.out.println("Funcionalidade alocada para o próximo escopo (TP3).");
                            break;
                        case "C":
                            listarTodosCursosPaginados();
                            break;
                        case "R":
                            break;
                        default:
                            System.out.println("Opção inválida.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro no menu de inscrições: " + e.getMessage());
            }
        }
    }

    private void buscarCursoPorCodigo() throws Exception {
        System.out.print("Digite o código (NanoID) do curso: ");
        String codigo = console.nextLine().trim();

        Curso c = arqCurso.readCodigo(codigo);
        if (c == null) {
            System.out.println("Curso não encontrado para o código fornecido.");
            return;
        }

        // CORREÇÃO: Utilizando o método dinâmico com a entidade Inscricao
        boolean jaInscrito = arqInscricao.buscarRelacao(Sessao.getIdUsuarioLogado(), c.getID()) != null;
        exibirDetalhesCurso(c, jaInscrito);
    }

    private void listarTodosCursosPaginados() throws Exception {
        ArrayList<Curso> todosCursos = arqCurso.listarCursosOrdenadosPorData();
        int totalElementos = todosCursos.size(); // CORREÇÃO: Escopo movido para fora do loop de renderização
        int itensPorPagina = 10;
        int totalPaginas = (int) Math.ceil((double) totalElementos / itensPorPagina);
        if (totalPaginas == 0)
            totalPaginas = 1;

        int paginaAtual = 1;
        String opcao = "";

        while (!opcao.equals("R")) {
            System.out.println("\nEntrePares 1.0 --------------");
            System.out.println("> Início > Minhas inscrições > Lista de cursos");
            System.out.println("Página " + paginaAtual + " de " + totalPaginas);

            int inicioIndice = (paginaAtual - 1) * itensPorPagina;
            int fimIndice = Math.min(inicioIndice + itensPorPagina, totalElementos);

            ArrayList<Curso> paginaCursos = new ArrayList<>();
            int contadorExibicao = 1;

            for (int i = inicioIndice; i < fimIndice; i++) {
                Curso c = todosCursos.get(i);
                paginaCursos.add(c);
                System.out.println("(" + contadorExibicao + ") " + c.getNome() + " - " + c.getDataInicio().format(dtf));
                contadorExibicao++;
            }

            if (totalElementos == 0) {
                System.out.println("[Nenhum curso cadastrado no sistema]");
            }

            System.out.println("\n(A) Página anterior");
            System.out.println("(B) Próxima página");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");
            opcao = console.nextLine().trim().toUpperCase();

            if (opcao.matches("\\d+")) {
                int idx = Integer.parseInt(opcao) - 1;
                if (idx >= 0 && idx < paginaCursos.size()) {
                    Curso selecionado = paginaCursos.get(idx);
                    boolean jaInscrito = arqInscricao.buscarRelacao(Sessao.getIdUsuarioLogado(),
                            selecionado.getID()) != null;
                    exibirDetalhesCurso(selecionado, jaInscrito);

                    // Recarrega de forma segura variáveis locais de paginação pós-contexto
                    todosCursos = arqCurso.listarCursosOrdenadosPorData();
                    totalElementos = todosCursos.size();
                    totalPaginas = (int) Math.ceil((double) totalElementos / itensPorPagina);
                    if (totalPaginas == 0)
                        totalPaginas = 1;
                } else {
                    System.out.println("Seleção fora dos limites da página atual.");
                }
            } else {
                switch (opcao) {
                    case "A":
                        if (paginaAtual > 1)
                            paginaAtual--;
                        else
                            System.out.println("Você já está na primeira página.");
                        break;
                    case "B":
                        if (paginaAtual < totalPaginas)
                            paginaAtual++;
                        else
                            System.out.println("Você já está na última página.");
                        break;
                    case "R":
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            }
        }
    }

    private void exibirDetalhesCurso(Curso c, boolean deMinhasInscricoes) throws Exception {
        System.out.println("\nEntrePares 1.0 --------------");
        System.out.println(
                "> Início > Minhas inscrições > " + (deMinhasInscricoes ? "" : "Lista de Cursos > ") + c.getNome());
        System.out.println("CÓDIGO........: " + c.getCodigo());
        System.out.println("CURSO.........: " + c.getNome());
        System.out.println("DESCRIÇÃO.....: " + c.getDescricao());
        System.out.println("DATA DE INÍCIO: " + c.getDataInicio().format(dtf));

        if (deMinhasInscricoes) {
            System.out.println("\n(A) Cancelar minha inscrição no curso");
        } else {
            System.out.println("\n(A) Fazer minha inscrição no curso");
        }
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        String opcao = console.nextLine().trim().toUpperCase();

        if (opcao.equals("A")) {
            if (deMinhasInscricoes) {
                // CORREÇÃO: Tipo alterado para Inscricao
                Inscricao relacao = arqInscricao.buscarRelacao(Sessao.getIdUsuarioLogado(), c.getID());
                if (relacao != null) {
                    arqInscricao.delete(relacao.getID());
                    System.out.println("Inscrição cancelada com sucesso!");
                } else {
                    System.out.println("Vínculo de matrícula não rastreado.");
                }
            } else {
                if (c.getIdUsuario() == Sessao.getIdUsuarioLogado()) {
                    System.out.println(
                            "Regra de integridade: Você é o proponente deste curso e não pode se inscrever nele.");
                    return;
                }
                if (c.getEstado() != 0) {
                    System.out.println("Inscrição recusada. Este curso não está ativo ou aceitando inscrições.");
                    return;
                }

                // CORREÇÃO: Tipo alterado para Inscricao
                Inscricao duplicada = arqInscricao.buscarRelacao(Sessao.getIdUsuarioLogado(), c.getID());
                if (duplicada != null) {
                    System.out.println("Inscrição recusada. Você já está matriculado neste curso.");
                    return;
                }

                // CORREÇÃO: Instanciando a classe Inscricao com parâmetros adequados ao seu
                // modelo
                Inscricao novaInscricao = new Inscricao();
                novaInscricao.setID(-1); // ID inicial genérico antes do incremento físico do arquivo
                novaInscricao.setIdCurso(c.getID());
                novaInscricao.setIdUsuario(Sessao.getIdUsuarioLogado());

                arqInscricao.create(novaInscricao);
                System.out.println("Inscrição realizada com sucesso!");
            }
        }
    }

    public void close() {
        try {
            if (arqCurso != null)
                arqCurso.close();
            if (arqInscricao != null)
                arqInscricao.close();
        } catch (Exception e) {
        }
    }
}