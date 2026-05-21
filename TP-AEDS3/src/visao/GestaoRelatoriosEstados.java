package visao;

import aed3.ArvoreBMais;
import arquivos.ArquivoCurso;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.CursoUsuario;
import entidades.EstadoCurso;
import entidades.Usuario;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GestaoRelatoriosEstados {

    private ArquivoCurso arqCursos;
    private ArquivoUsuario arqUsuarios;

    // Agora podemos tipar a árvore corretamente com CursoUsuario!
    private ArvoreBMais<CursoUsuario> indiceCursoInscritos;

    public GestaoRelatoriosEstados(ArquivoCurso arqCursos,
            ArquivoUsuario arqUsuarios,
            ArvoreBMais<CursoUsuario> indiceCursoInscritos) {
        this.arqCursos = arqCursos;
        this.arqUsuarios = arqUsuarios;
        this.indiceCursoInscritos = indiceCursoInscritos;
    }

    public boolean validarPermissaoInscricao(int idCurso) throws Exception {
        Curso curso = arqCursos.read(idCurso);
        if (curso == null) {
            System.out.println("Erro: Curso não encontrado.");
            return false;
        }

        EstadoCurso estado = EstadoCurso.fromCodigo(curso.getEstado());
        if (estado != EstadoCurso.ABERTO) {
            System.out.println("Inscrição bloqueada. Motivo: " + estado.getDescricao());
            return false;
        }
        return true;
    }

    public void exibirInscritosDoCurso(int idCurso) throws Exception {
        CursoUsuario chaveBusca = new CursoUsuario();
        chaveBusca.setIdCurso(idCurso);

        // Chamada limpa, tipada e sem gambiarras de compilação
        ArrayList<CursoUsuario> listaRelacionamentos = indiceCursoInscritos.read(chaveBusca);

        if (listaRelacionamentos == null || listaRelacionamentos.isEmpty()) {
            System.out.println("Não há inscritos neste curso no momento.");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int contador = 1;

        System.out.println("\n--- ALUNOS INSCRITOS ---");
        for (CursoUsuario relacionamento : listaRelacionamentos) {
            if (relacionamento != null) {
                Usuario aluno = arqUsuarios.read(relacionamento.getIdUsuario());
                if (aluno != null) {
                    String dataFormatada = relacionamento.getDataInscricao().format(dtf);
                    System.out.printf("(%d) %s (%s)\n", contador, aluno.getNome(), dataFormatada);
                    contador++;
                }
            }
        }
    }

    public void exportarInscritosCSV(int idCurso) {
        try {
            Curso curso = arqCursos.read(idCurso);
            if (curso == null) {
                System.out.println("Curso não encontrado para exportação.");
                return;
            }

            String nomeArquivo = "inscritos_curso_" + idCurso + ".csv";

            CursoUsuario chaveBusca = new CursoUsuario();
            chaveBusca.setIdCurso(idCurso);

            ArrayList<CursoUsuario> listaRelacionamentos = indiceCursoInscritos.read(chaveBusca);

            if (listaRelacionamentos == null || listaRelacionamentos.isEmpty()) {
                System.out.println("Exportação cancelada: Nenhum aluno inscrito.");
                return;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
                writer.println("Nome,Email,DataDaInscricao");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (CursoUsuario relacionamento : listaRelacionamentos) {
                    if (relacionamento != null) {
                        Usuario aluno = arqUsuarios.read(relacionamento.getIdUsuario());
                        if (aluno != null) {
                            String nomeLimpo = aluno.getNome().replace(",", "");
                            String emailLimpo = aluno.getEmail().replace(",", "");
                            String dataFormatada = relacionamento.getDataInscricao().format(dtf);
                            writer.printf("%s,%s,%s\n", nomeLimpo, emailLimpo, dataFormatada);
                        }
                    }
                }
                System.out.println("Sucesso! Lista exportada para o arquivo: " + nomeArquivo);
            }
        } catch (Exception e) {
            System.out.println("Erro crítico ao gerar o arquivo CSV de relatórios: " + e.getMessage());
        }
    }
}