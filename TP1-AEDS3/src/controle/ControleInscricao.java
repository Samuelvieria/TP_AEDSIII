package controle;

import arquivos.ArquivoCurso;
import arquivos.ArquivoInscricao;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Inscricao;
import entidades.Usuario;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import visao.ContextoDetalhe;
import visao.VisaoInscricao;

public class ControleInscricao {

    private static final int TAM_PAGINA = 10;

    private ArquivoCurso arqCurso;
    private ArquivoUsuario arqUsuario;
    private ArquivoInscricao arqInscricao;
    private VisaoInscricao visao;

    public ControleInscricao(Scanner console) throws Exception {
        this.visao = new VisaoInscricao(console);
        this.arqCurso = new ArquivoCurso();
        this.arqUsuario = new ArquivoUsuario();
        this.arqInscricao = new ArquivoInscricao();
    }

    public void menuMinhasInscricoes() {
        String opcao;
        do {
            ArrayList<Curso> inscritos;
            ArrayList<String> sufixos;
            try {
                inscritos = listarCursosInscritos();
                sufixos = montarSufixosEstado(inscritos);
            } catch (Exception e) {
                visao.mostrarMensagem("Erro ao carregar inscrições: " + e.getMessage());
                inscritos = new ArrayList<>();
                sufixos = new ArrayList<>();
            }

            visao.menuMinhasInscricoes(inscritos, sufixos);
            opcao = visao.lerOpcaoMenu();

            if (opcao.matches("\\d+")) {
                int indice = Integer.parseInt(opcao) - 1;
                if (indice >= 0 && indice < inscritos.size()) {
                    Curso curso = inscritos.get(indice);
                    String breadcrumb = "> Início > Minhas inscrições > " + curso.getNome();
                    gerenciarDetalhe(curso, breadcrumb);
                } else {
                    visao.mostrarMensagem("Inscrição inválida.");
                }
            } else {
                switch (opcao) {
                    case "A": buscarPorCodigo(); break;
                    case "B": visao.mostrarMensagem("Busca por palavras-chave disponível apenas no TP3."); break;
                    case "C": listarTodosPaginado(); break;
                    case "R": break;
                    default: visao.mostrarMensagem("Opção inválida.");
                }
            }
        } while (!opcao.equals("R"));
    }

    private void buscarPorCodigo() {
        String codigo = visao.lerCodigoCurso();
        if (codigo.isEmpty()) {
            visao.mostrarMensagem("Código não informado.");
            return;
        }
        try {
            Curso curso = arqCurso.readCodigo(codigo);
            if (curso == null || !curso.getCodigo().equals(codigo)) {
                visao.mostrarMensagem("Curso não encontrado para o código informado.");
                return;
            }
            String breadcrumb = "> Início > Minhas inscrições > " + curso.getNome();
            gerenciarDetalhe(curso, breadcrumb);
        } catch (Exception e) {
            visao.mostrarMensagem("Erro na busca: " + e.getMessage());
        }
    }

    private void listarTodosPaginado() {
        ArrayList<Curso> todos;
        try {
            todos = arqCurso.listarTodos();
            todos.sort(Comparator.comparing(Curso::getDataInicio));
        } catch (Exception e) {
            visao.mostrarMensagem("Erro ao carregar cursos: " + e.getMessage());
            return;
        }
        if (todos.isEmpty()) {
            visao.mostrarMensagem("Nenhum curso cadastrado.");
            return;
        }

        int totalPaginas = (int) Math.ceil((double) todos.size() / TAM_PAGINA);
        int paginaAtual = 1;
        String breadcrumbLista = "> Início > Minhas inscrições > Lista de cursos";

        String opcao;
        do {
            int inicio = (paginaAtual - 1) * TAM_PAGINA;
            int fim = Math.min(inicio + TAM_PAGINA, todos.size());
            ArrayList<Curso> pagina = new ArrayList<>(todos.subList(inicio, fim));

            visao.exibirListaPaginada(breadcrumbLista, paginaAtual, totalPaginas, pagina);
            opcao = visao.lerOpcaoLista();

            if (opcao.equals("A")) {
                if (paginaAtual > 1) paginaAtual--;
                else visao.mostrarMensagem("Você já está na primeira página.");
            } else if (opcao.equals("B")) {
                if (paginaAtual < totalPaginas) paginaAtual++;
                else visao.mostrarMensagem("Você já está na última página.");
            } else if (opcao.matches("[0-9]")) {
                Curso selecionado = cursoDaPagina(pagina, opcao.charAt(0));
                if (selecionado == null) {
                    visao.mostrarMensagem("Opção inválida para esta página.");
                } else {
                    String breadcrumbDetalhe = breadcrumbLista + " > " + selecionado.getNome();
                    gerenciarDetalhe(selecionado, breadcrumbDetalhe);
                }
            } else if (!opcao.equals("R")) {
                visao.mostrarMensagem("Opção inválida.");
            }
        } while (!opcao.equals("R"));
    }

    private Curso cursoDaPagina(ArrayList<Curso> pagina, char digito) {
        if (digito == '0') {
            return pagina.size() == TAM_PAGINA ? pagina.get(9) : null;
        }
        int indice = digito - '1';
        if (indice >= 0 && indice < pagina.size() && indice < 9) {
            return pagina.get(indice);
        }
        return null;
    }

    private void gerenciarDetalhe(Curso curso, String breadcrumb) {
        String opcao;
        do {
            ContextoDetalhe contexto;
            try {
                contexto = contextoParaCurso(curso);
            } catch (Exception e) {
                visao.mostrarMensagem("Erro: " + e.getMessage());
                return;
            }

            visao.exibirDetalheCurso(breadcrumb, curso, resolverNomeAutor(curso), contexto);
            opcao = visao.lerOpcaoDetalhe();

            if (opcao.equals("A")) {
                if (contexto == ContextoDetalhe.INSCREVER) {
                    efetivarInscricao(curso);
                } else {
                    cancelarInscricao(curso);
                }
            } else if (!opcao.equals("R")) {
                visao.mostrarMensagem("Opção inválida.");
            }
        } while (!opcao.equals("R"));
    }

    private void efetivarInscricao(Curso curso) {
        int idUsuario = Sessao.getIdUsuarioLogado();
        String erro = validarInscricao(curso, idUsuario);
        if (erro != null) {
            visao.mostrarMensagem(erro);
            return;
        }
        try {
            arqInscricao.create(new Inscricao(idUsuario, curso.getID()));
            visao.mostrarMensagem("Inscrição realizada com sucesso!");
        } catch (Exception e) {
            visao.mostrarMensagem(e.getMessage());
        }
    }

    private void cancelarInscricao(Curso curso) {
        try {
            Inscricao inscricao = buscarInscricao(Sessao.getIdUsuarioLogado(), curso.getID());
            if (inscricao == null) {
                visao.mostrarMensagem("Inscrição não encontrada.");
                return;
            }
            if (arqInscricao.delete(inscricao.getID())) {
                visao.mostrarMensagem("Inscrição cancelada com sucesso.");
            } else {
                visao.mostrarMensagem("Falha ao cancelar a inscrição.");
            }
        } catch (Exception e) {
            visao.mostrarMensagem("Erro ao cancelar: " + e.getMessage());
        }
    }

    private ArrayList<Curso> listarCursosInscritos() throws Exception {
        ArrayList<Curso> cursos = new ArrayList<>();
        for (Inscricao ins : arqInscricao.listarPorUsuario(Sessao.getIdUsuarioLogado())) {
            Curso c = arqCurso.read(ins.getIdCurso());
            if (c != null) {
                cursos.add(c);
            }
        }
        cursos.sort(Comparator.comparing(Curso::getDataInicio));
        return cursos;
    }

    private ArrayList<String> montarSufixosEstado(ArrayList<Curso> cursos) {
        ArrayList<String> sufixos = new ArrayList<>();
        for (Curso c : cursos) {
            sufixos.add(sufixoEstadoCurso(c));
        }
        return sufixos;
    }

    // Adaptado para usar os métodos lógicos corretos do modelo unificado
    private String sufixoEstadoCurso(Curso c) {
        if (c.inscricoesEncerradas()) return " (INSCRIÇÕES ENCERRADAS)";
        if (c.estaConcluido()) return " (CURSO REALIZADO)";
        if (c.estaCancelado()) return " (CURSO CANCELADO)";
        return "";
    }

    private ContextoDetalhe contextoParaCurso(Curso curso) throws Exception {
        return buscarInscricao(Sessao.getIdUsuarioLogado(), curso.getID()) != null
                ? ContextoDetalhe.CANCELAR : ContextoDetalhe.INSCREVER;
    }

    // Otimizado: Busca linear mantida apenas se a Árvore B+ de ParUsuarioInscricao retornar múltiplos
    private Inscricao buscarInscricao(int idUsuario, int idCurso) throws Exception {
        ArrayList<Inscricao> inscricoes = arqInscricao.listarPorUsuario(idUsuario);
        for (Inscricao ins : inscricoes) {
            if (ins.getIdCurso() == idCurso) {
                return ins;
            }
        }
        return null;
    }

    // Refatorado para usar as propriedades dinâmicas do objeto do TP2
    private String validarInscricao(Curso curso, int idUsuario) {
        if (curso.getIdUsuario() == idUsuario) {
            return "Você não pode se inscrever no seu próprio curso.";
        }
        if (curso.inscricoesEncerradas()) {
            return "As inscrições estão encerradas para este curso.";
        }
        if (curso.estaConcluido()) {
            return "Este curso já foi realizado.";
        }
        if (curso.estaCancelado()) {
            return "Este curso foi cancelado.";
        }
        return null;
    }

    private String resolverNomeAutor(Curso curso) {
        try {
            Usuario autor = arqUsuario.read(curso.getIdUsuario());
            if (autor != null && autor.getNome() != null && !autor.getNome().isEmpty()) {
                return autor.getNome();
            }
        } catch (Exception ignored) { }
        return "Desconhecido";
    }

    public void close() throws Exception {
        if (arqCurso != null) arqCurso.close();
        if (arqUsuario != null) arqUsuario.close();
        if (arqInscricao != null) arqInscricao.close();
    }
}
