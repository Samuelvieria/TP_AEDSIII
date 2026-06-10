package testes;

import arquivos.ArquivoCurso;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Usuario;
import indices.Texto;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

// TESTE DO ÍNDICE INVERTIDO E DA BUSCA POR PALAVRAS-CHAVE (TF x IDF)
// Reproduz o exemplo de cursos do enunciado do TP3 e valida:
// - extração de termos (stop words, acentos e caixa baixa);
// - manutenção das listas invertidas em create/update/delete;
// - ordenação dos resultados pelo valor TF x IDF;
// - busca por código compartilhável.
public class TesteIndiceInvertido {

    private static int testesPassados = 0;
    private static int totalTestes = 0;
    private static int contadorCodigo = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   TESTE DO ÍNDICE INVERTIDO E BUSCA TF x IDF     ");
        System.out.println("==================================================");

        limparArquivos();

        try {
            ArquivoUsuario arqUsuario = new ArquivoUsuario();
            ArquivoCurso arqCurso = new ArquivoCurso();

            Usuario professor = new Usuario("Professor", "professor@email.com", "1234", "Pergunta", "Resposta");
            int idProfessor = arqUsuario.create(professor);

            // ==================================================
            // CENÁRIO 1: Extração de termos (stop words, acentos e caixa baixa)
            // ==================================================
            System.out.println("\n[Cenário 1] Extração de termos do nome do curso");

            ArrayList<String> termos1 = Texto.extrairTermos("Introdução à Inteligência Artificial");
            Verificar(termos1.equals(Arrays.asList("introducao", "inteligencia", "artificial")),
                    "Stop words removidas e termos normalizados (sem acento/minúsculas): " + termos1);

            ArrayList<String> termos3 = Texto.extrairTermos("Inteligência no Trabalho por Meio da Inteligência Artificial");
            Verificar(termos3.equals(Arrays.asList("inteligencia", "trabalho", "meio", "inteligencia", "artificial")),
                    "Numerais e preposições descartados, repetições preservadas: " + termos3);

            // ==================================================
            // CENÁRIO 2: Cadastro dos cursos de exemplo do enunciado
            // ==================================================
            System.out.println("\n[Cenário 2] Cadastro dos cursos de exemplo");

            Curso c1 = novoCurso("Introdução à Inteligência Artificial", idProfessor);
            Curso c2 = novoCurso("Inteligência Emocional para Gestores", idProfessor);
            Curso c3 = novoCurso("Inteligência no Trabalho por Meio da Inteligência Artificial", idProfessor);
            Curso c4 = novoCurso("Introdução à Gestão de Equipes", idProfessor);

            int id1 = arqCurso.create(c1);
            int id2 = arqCurso.create(c2);
            int id3 = arqCurso.create(c3);
            int id4 = arqCurso.create(c4);

            Verificar(id1 > 0 && id2 > 0 && id3 > 0 && id4 > 0,
                    "4 cursos do exemplo cadastrados com sucesso e indexados.");

            // ==================================================
            // CENÁRIO 3: Busca por palavras-chave ordenada por TF x IDF
            // ==================================================
            System.out.println("\n[Cenário 3] Busca por palavras-chave ordenada por TF x IDF");

            ArrayList<Curso> resultado = arqCurso.buscarPorPalavras("Inteligência Artificial");

            Verificar(resultado.size() == 3,
                    "Busca por 'Inteligência Artificial' retornou 3 cursos (curso 4 não contém os termos).");

            boolean ordemCorreta = resultado.size() == 3
                    && resultado.get(0).getID() == id1
                    && resultado.get(1).getID() == id3
                    && resultado.get(2).getID() == id2;
            Verificar(ordemCorreta,
                    "Ranking TF x IDF segue a ordem esperada do enunciado: [curso1, curso3, curso2].");

            ArrayList<Curso> semResultado = arqCurso.buscarPorPalavras("Culinária");
            Verificar(semResultado.isEmpty(),
                    "Busca por termo inexistente não retorna nenhum curso.");

            // ==================================================
            // CENÁRIO 4: Atualização do nome reindexa as listas invertidas
            // ==================================================
            System.out.println("\n[Cenário 4] Atualização do nome reindexa as listas invertidas");

            c2.setNome("Gestão de Equipes Remotas");
            arqCurso.update(c2);

            ArrayList<Curso> buscaTermoAntigo = arqCurso.buscarPorPalavras("Emocional");
            Verificar(buscaTermoAntigo.isEmpty(),
                    "Termo antigo ('emocional') deixa de retornar o curso após a renomeação.");

            ArrayList<Curso> buscaTermoNovo = arqCurso.buscarPorPalavras("Remotas");
            boolean encontrouTermoNovo = buscaTermoNovo.size() == 1 && buscaTermoNovo.get(0).getID() == id2;
            Verificar(encontrouTermoNovo,
                    "Novo termo ('remotas') passa a indexar o curso renomeado.");

            // ==================================================
            // CENÁRIO 5: Exclusão do curso remove suas entradas do índice
            // ==================================================
            System.out.println("\n[Cenário 5] Exclusão do curso remove entradas do índice invertido");

            arqCurso.delete(id4);
            ArrayList<Curso> buscaCursoExcluido = arqCurso.buscarPorPalavras("Equipes");
            Verificar(buscaCursoExcluido.isEmpty(),
                    "Curso excluído ('Equipes') não aparece mais nas buscas por palavras-chave.");

            // ==================================================
            // CENÁRIO 6: Busca por código compartilhável
            // ==================================================
            System.out.println("\n[Cenário 6] Busca por código compartilhável");

            Curso porCodigo = arqCurso.readCodigo(c1.getCodigo());
            Verificar(porCodigo != null && porCodigo.getID() == id1,
                    "Busca por código compartilhável retorna o curso correto.");

            arqUsuario.close();
            arqCurso.close();

            relatorioFinal();
        } catch (Exception e) {
            System.err.println("\n❌ ERRO FATAL DE EXECUÇÃO NO TESTADOR:");
            e.printStackTrace();
        }
    }

    // Cria um curso de teste com código compartilhável único de 10 caracteres
    private static Curso novoCurso(String nome, int idUsuario) {
        contadorCodigo++;
        String codigo = String.format("INV%07d", contadorCodigo); // 10 caracteres
        return new Curso(nome, "Descrição de teste", LocalDate.of(2026, 1, 1), codigo, (byte) 0, idUsuario);
    }

    private static void Verificar(boolean condicao, String mensagem) {
        totalTestes++;
        if (condicao) {
            System.out.println("   🟢 PASSOU: " + mensagem);
            testesPassados++;
        } else {
            System.out.println("   🔴 FALHOU: " + mensagem);
        }
    }

    private static void relatorioFinal() {
        System.out.println("\n==================================================");
        System.out.println("              RESULTADO DOS TESTES                ");
        System.out.println("==================================================");
        System.out.println("Testes Executados: " + totalTestes);
        System.out.println("Testes Passados:   " + testesPassados);
        System.out.println("Testes Falhados:   " + (totalTestes - testesPassados));

        if (testesPassados == totalTestes) {
            System.out.println("\n🔥 PARABÉNS! O índice invertido e a busca TF x IDF passaram em todos os testes.");
        } else {
            System.out.println("\n⚠️ ATENÇÃO: Corrija as falhas apontadas acima.");
        }
        System.out.println("==================================================");
    }

    // Remove os arquivos de dados para garantir um cenário limpo e previsível
    private static void limparArquivos() {
        String[] caminhos = {
                "dados/cursos",
                "dados/usuarios",
                "dados/inscricoes",
                "dados/cursos_codigo.idx",
                "dados/cursos_codigo.dir",
                "dados/cursos_nome.db",
                "dados/cursos_usuario.db",
                "dados/cursos_invertido.db",
                "dados/usuarios_email.idx",
                "dados/usuarios_email.dir",
                "dados/inscricoes_usuario.db",
                "dados/inscricoes_curso.db"
        };
        for (String caminho : caminhos)
            apagar(new File(caminho));
    }

    private static void apagar(File arquivo) {
        if (!arquivo.exists())
            return;
        if (arquivo.isDirectory()) {
            File[] filhos = arquivo.listFiles();
            if (filhos != null)
                for (File filho : filhos)
                    apagar(filho);
        }
        arquivo.delete();
    }
}
