package controle;

import arquivos.ArquivoCurso;
import arquivos.ArquivoUsuario;
import entidades.Curso;
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
    private VisaoInscricao visao;

    public ControleInscricao(Scanner console) throws Exception {
        this.visao = new VisaoInscricao(console);
        this.arqCurso = new ArquivoCurso();
        this.arqUsuario = new ArquivoUsuario();
    }

    public void menuMinhasInscricoes() {
        String opcao;
        do {
            visao.menuMinhasInscricoes();
            opcao = visao.lerOpcaoMenu();
            switch (opcao) {
                case "A": buscarPorCodigo(); break;
                case "B": visao.mostrarMensagem("Busca por palavras-chave disponível apenas no TP3."); break;
                case "C": listarTodosPaginado(); break;
                case "R": break;
                default: visao.mostrarMensagem("Opção inválida.");
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
            gerenciarDetalhe(curso, breadcrumb, ContextoDetalhe.INSCREVER);
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
                    gerenciarDetalhe(selecionado, breadcrumbDetalhe, ContextoDetalhe.INSCREVER);
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

    private void gerenciarDetalhe(Curso curso, String breadcrumb, ContextoDetalhe contexto) {
        String opcao;
        do {
            visao.exibirDetalheCurso(breadcrumb, curso, resolverNomeAutor(curso), contexto);
            opcao = visao.lerOpcaoDetalhe();
            if (opcao.equals("A")) {
                if (contexto == ContextoDetalhe.INSCREVER) {
                    visao.mostrarMensagem(
                            "A efetivação da inscrição será implementada na próxima etapa do TP2.");
                } else {
                    visao.mostrarMensagem(
                            "O cancelamento da inscrição será implementado na próxima etapa do TP2.");
                }
            } else if (!opcao.equals("R")) {
                visao.mostrarMensagem("Opção inválida.");
            }
        } while (!opcao.equals("R"));
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
        arqCurso.close();
        arqUsuario.close();
    }
}
