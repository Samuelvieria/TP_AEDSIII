package visao;

import arquivos.ArquivoCurso;
import arquivos.ArquivoInscricao;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Inscricao;
import entidades.Usuario;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// GESTÃO DE RELATÓRIOS DE INSCRITOS
// Responsável por listar e exportar os usuários inscritos em um curso,
// utilizando a Árvore B+ curso -> inscrições (ArquivoInscricao).
public class GestaoRelatoriosEstados {

    private ArquivoCurso arqCursos;
    private ArquivoUsuario arqUsuarios;
    private ArquivoInscricao arqInscricoes;

    private static final SimpleDateFormat FMT_DATA = new SimpleDateFormat("dd/MM/yyyy");

    public GestaoRelatoriosEstados(ArquivoCurso arqCursos, ArquivoUsuario arqUsuarios, ArquivoInscricao arqInscricoes) {
        this.arqCursos = arqCursos;
        this.arqUsuarios = arqUsuarios;
        this.arqInscricoes = arqInscricoes;
    }

    // Exibe a lista de usuários inscritos no curso informado.
    public void exibirInscritosDoCurso(int idCurso) throws Exception {
        ArrayList<Inscricao> inscritos = arqInscricoes.listarPorCurso(idCurso);

        if (inscritos.isEmpty()) {
            System.out.println("Não há inscritos neste curso no momento.");
            return;
        }

        System.out.println("\n--- ALUNOS INSCRITOS ---");
        int contador = 1;
        for (Inscricao inscricao : inscritos) {
            Usuario aluno = arqUsuarios.read(inscricao.getIdUsuario());
            if (aluno != null) {
                String dataFormatada = FMT_DATA.format(new Date(inscricao.getDataInscricao()));
                System.out.printf("(%d) %s - %s (inscrito em %s)\n",
                        contador, aluno.getNome(), aluno.getEmail(), dataFormatada);
                contador++;
            }
        }
    }

    // Exporta a lista de inscritos do curso para um arquivo CSV.
    public void exportarInscritosCSV(int idCurso) {
        try {
            Curso curso = arqCursos.read(idCurso);
            if (curso == null) {
                System.out.println("Curso não encontrado para exportação.");
                return;
            }

            ArrayList<Inscricao> inscritos = arqInscricoes.listarPorCurso(idCurso);
            if (inscritos.isEmpty()) {
                System.out.println("Exportação cancelada: Nenhum aluno inscrito.");
                return;
            }

            String nomeArquivo = "inscritos_curso_" + idCurso + ".csv";

            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
                writer.println("Nome,Email,DataDaInscricao");
                for (Inscricao inscricao : inscritos) {
                    Usuario aluno = arqUsuarios.read(inscricao.getIdUsuario());
                    if (aluno != null) {
                        String nomeLimpo = aluno.getNome().replace(",", "");
                        String emailLimpo = aluno.getEmail().replace(",", "");
                        String dataFormatada = FMT_DATA.format(new Date(inscricao.getDataInscricao()));
                        writer.printf("%s,%s,%s\n", nomeLimpo, emailLimpo, dataFormatada);
                    }
                }
                System.out.println("Sucesso! Lista exportada para o arquivo: " + nomeArquivo);
            }
        } catch (Exception e) {
            System.out.println("Erro crítico ao gerar o arquivo CSV de relatórios: " + e.getMessage());
        }
    }
}
