package testes;

import indices.ListaInvertida;
import indices.ParCursoTF;
import aed3.TratamentoTexto;
import java.util.List;

/**
 * Teste básico da ListaInvertida (Parte 2)
 * Valida a adição, remoção e busca de termos
 */
public class TesteListaInvertida {

    public static void main(String[] args) throws Exception {
        System.out.println("=== TESTE DA LISTA INVERTIDA (PARTE 2) ===\n");

        ListaInvertida indice = new ListaInvertida();

        // ====== TESTE 1: Adicionar termos ======
        System.out.println("TESTE 1: Adicionando termos ao índice...");
        try {
            // Simular 3 cursos com nomes diferentes
            String curso1 = "Introdução à Inteligência Artificial";
            String curso2 = "Machine Learning e Redes Neurais";
            String curso3 = "Inteligência Artificial Avançada";

            // Extrair termos e calcular TF
            List<String> termosCurso1 = TratamentoTexto.extrairTermos(curso1);
            List<String> termosCurso2 = TratamentoTexto.extrairTermos(curso2);
            List<String> termosCurso3 = TratamentoTexto.extrairTermos(curso3);

            System.out.println("Curso 1 - Termos extraídos: " + termosCurso1);
            System.out.println("Curso 2 - Termos extraídos: " + termosCurso2);
            System.out.println("Curso 3 - Termos extraídos: " + termosCurso3);

            // Adicionar termos ao índice
            for (String termo : termosCurso1) {
                float tf = TratamentoTexto.calcularTF(curso1, termo);
                indice.adicionarTermo(termo, 1, tf);
            }
            for (String termo : termosCurso2) {
                float tf = TratamentoTexto.calcularTF(curso2, termo);
                indice.adicionarTermo(termo, 2, tf);
            }
            for (String termo : termosCurso3) {
                float tf = TratamentoTexto.calcularTF(curso3, termo);
                indice.adicionarTermo(termo, 3, tf);
            }

            // Atualizar total de cursos
            indice.atualizarTotalCursos(3);
            System.out.println("✓ Termos adicionados com sucesso!\n");

        } catch (Exception e) {
            System.out.println("✗ Erro ao adicionar termos: " + e.getMessage());
            e.printStackTrace();
        }

        // ====== TESTE 2: Buscar termos ======
        System.out.println("TESTE 2: Buscando termos no índice...");
        try {
            String termoParaBuscar = "inteligencia";
            List<ParCursoTF> resultado = indice.buscarTermo(termoParaBuscar);

            System.out.println("Buscando pelo termo '" + termoParaBuscar + "':");
            System.out.println("Quantidade de cursos encontrados: " + resultado.size());

            for (ParCursoTF par : resultado) {
                System.out.println("  ID Curso: " + par.getIdCurso() + ", TF: " + par.getTF());
            }

            // Verificar quantidade de cursos com o termo
            int qtdCursosComTermo = indice.obterQuantidadeCursosComTermo(termoParaBuscar);
            System.out.println("Quantidade total de cursos com '" + termoParaBuscar + "': " 
                    + qtdCursosComTermo);
            System.out.println("✓ Busca realizada com sucesso!\n");

        } catch (Exception e) {
            System.out.println("✗ Erro ao buscar termos: " + e.getMessage());
            e.printStackTrace();
        }

        // ====== TESTE 3: Calcular IDF ======
        System.out.println("TESTE 3: Calculando IDF para teste...");
        try {
            String termo = "inteligencia";
            int totalCursos = indice.obterTotalCursos();
            int cursosComTermo = indice.obterQuantidadeCursosComTermo(termo);

            float idf = TratamentoTexto.calcularIDF(totalCursos, cursosComTermo);
            System.out.println("Para o termo '" + termo + "':");
            System.out.println("  Total de cursos: " + totalCursos);
            System.out.println("  Cursos com o termo: " + cursosComTermo);
            System.out.println("  IDF = log10(" + totalCursos + "/" + cursosComTermo + ") + 1 = " + idf);

            // Calcular TFxIDF para cada curso
            List<ParCursoTF> cursosComTernoResultado = indice.buscarTermo(termo);
            System.out.println("  Scores TFxIDF:");
            for (ParCursoTF par : cursosComTernoResultado) {
                float tfidf = TratamentoTexto.calcularTFxIDF(par.getTF(), idf);
                System.out.println("    Curso " + par.getIdCurso() + ": TF=" + par.getTF() 
                        + " * IDF=" + idf + " = " + tfidf);
            }

            System.out.println("✓ IDF calculado com sucesso!\n");

        } catch (Exception e) {
            System.out.println("✗ Erro ao calcular IDF: " + e.getMessage());
            e.printStackTrace();
        }

        // ====== TESTE 4: Remover termo de um curso ======
        System.out.println("TESTE 4: Removendo termo de um curso...");
        try {
            String termo = "machine";
            int idCurso = 2;

            System.out.println("Removendo termo '" + termo + "' do curso " + idCurso);
            indice.removerTermo(termo, idCurso);

            List<ParCursoTF> resultado = indice.buscarTermo(termo);
            System.out.println("Após remoção, cursos com '" + termo + "': " + resultado.size());
            for (ParCursoTF par : resultado) {
                System.out.println("  ID Curso: " + par.getIdCurso() + ", TF: " + par.getTF());
            }

            System.out.println("✓ Remoção realizada com sucesso!\n");

        } catch (Exception e) {
            System.out.println("✗ Erro ao remover termo: " + e.getMessage());
            e.printStackTrace();
        }

        // ====== TESTE 5: Tratamento de texto ======
        System.out.println("TESTE 5: Testando tratamento de texto...");
        try {
            String texto = "Introdução à Programação e Lógica";

            System.out.println("Texto original: '" + texto + "'");

            String normalizado = TratamentoTexto.normalizar(texto);
            System.out.println("Texto normalizado: '" + normalizado + "'");

            List<String> termos = TratamentoTexto.extrairTermos(texto);
            System.out.println("Termos extraídos: " + termos);

            // Verificar stop words
            System.out.println("Teste de stop words:");
            System.out.println("  'à' é stop word? " + TratamentoTexto.ehStopWord("à"));
            System.out.println("  'programacao' é stop word? " + TratamentoTexto.ehStopWord("programacao"));
            System.out.println("  'e' é stop word? " + TratamentoTexto.ehStopWord("e"));

            System.out.println("✓ Tratamento de texto funcionando!\n");

        } catch (Exception e) {
            System.out.println("✗ Erro no tratamento de texto: " + e.getMessage());
            e.printStackTrace();
        }

        // Fechar o índice
        indice.fechar();
        System.out.println("=== TESTES CONCLUÍDOS ===");
    }
}
