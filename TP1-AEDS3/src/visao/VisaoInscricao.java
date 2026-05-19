package visao;

import entidades.Curso;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class VisaoInscricao {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Scanner console;

    public VisaoInscricao(Scanner console) {
        this.console = console;
    }

    public void exibirCabecalho(String breadcrumb) {
        System.out.println("\n\nEntrePares 1.0");
        System.out.println("--------------");
        System.out.println("\n" + breadcrumb + "\n");
    }

    public void menuMinhasInscricoes() {
        exibirCabecalho("> Início > Minhas inscrições");
        System.out.println("INSCRIÇÕES\n");
        System.out.println("(A) Buscar curso por código");
        System.out.println("(B) Buscar curso por palavras-chave");
        System.out.println("(C) Listar todos os cursos");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
    }

    public String lerOpcaoMenu() {
        return console.nextLine().trim().toUpperCase();
    }

    public String lerCodigoCurso() {
        System.out.print("\nInforme o código do curso (10 caracteres): ");
        return console.nextLine().trim();
    }

    public void exibirListaPaginada(String breadcrumb, int pagina, int totalPaginas,
            ArrayList<Curso> cursosPagina) {
        exibirCabecalho(breadcrumb);
        System.out.println("Página " + pagina + " de " + totalPaginas + "\n");

        for (int i = 0; i < cursosPagina.size(); i++) {
            Curso c = cursosPagina.get(i);
            String rotulo = (i < 9) ? String.valueOf(i + 1) : "0";
            System.out.printf("(%s) %s - %s\n", rotulo, c.getNome(),
                    c.getDataInicio().format(FMT_DATA));
        }

        System.out.println("\n(A) Página anterior");
        System.out.println("(B) Próxima página");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
    }

    public String lerOpcaoLista() {
        return console.nextLine().trim().toUpperCase();
    }

    public void exibirDetalheCurso(String breadcrumb, Curso curso, String nomeAutor,
            ContextoDetalhe contexto) {
        exibirCabecalho(breadcrumb);
        System.out.println("CÓDIGO........: " + curso.getCodigo());
        System.out.println("CURSO.........: " + curso.getNome());
        System.out.println("AUTOR.........: " + nomeAutor);
        System.out.println("DESCRIÇÃO.....: " + curso.getDescricao());
        System.out.println("DATA DE INÍCIO: " + curso.getDataInicio().format(FMT_DATA));
        System.out.println();

        if (contexto == ContextoDetalhe.INSCREVER) {
            System.out.println("(A) Fazer minha inscrição no curso");
        } else {
            System.out.println("(A) Cancelar minha inscrição no curso");
        }
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
    }

    public String lerOpcaoDetalhe() {
        return console.nextLine().trim().toUpperCase();
    }

    public void mostrarMensagem(String mensagem) {
        System.out.println("\n" + mensagem);
    }
}
