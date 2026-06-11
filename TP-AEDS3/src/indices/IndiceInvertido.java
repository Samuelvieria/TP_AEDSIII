package indices;

import aed3.ArvoreBMais;
import entidades.Curso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

// ÍNDICE INVERTIDO
// Mantém, para cada termo extraído dos nomes dos cursos, a lista de cursos (e
// respectivos valores de TF) em que o termo aparece. Permite buscar cursos por
// palavras-chave, retornando os resultados ordenados pelo valor TF x IDF.
public class IndiceInvertido {

    private ArvoreBMais<ParTermoId> arvore;

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public IndiceInvertido(String caminho) throws Exception {
        arvore = new ArvoreBMais<>(ParTermoId.class.getConstructor(), 5, caminho);
    }

    // ------------------------------------------------------------------------------
    // MANUTENÇÃO DAS LISTAS INVERTIDAS
    // ------------------------------------------------------------------------------

    // Indexa os termos do nome de um curso, calculando o TF de cada termo
    public void indexar(Curso curso) throws Exception {
        ArrayList<String> termos = Texto.extrairTermos(curso.getNome());
        if (termos.isEmpty())
            return;

        // Conta as ocorrências de cada termo no nome do curso
        Map<String, Integer> ocorrencias = new HashMap<>();
        for (String termo : termos)
            ocorrencias.merge(termo, 1, Integer::sum);

        int totalTermos = termos.size();
        for (Map.Entry<String, Integer> entrada : ocorrencias.entrySet()) {
            float tf = (float) entrada.getValue() / totalTermos;
            arvore.create(new ParTermoId(entrada.getKey(), curso.getID(), tf));
        }

        if (DEBUG) System.out.println("[DEBUG] Curso " + curso.getID() + " indexado com termos: " + ocorrencias.keySet());
    }

    // Remove do índice todas as entradas referentes ao curso
    public void remover(Curso curso) throws Exception {
        ArrayList<String> termos = Texto.extrairTermos(curso.getNome());
        Set<String> termosUnicos = new HashSet<>(termos);

        for (String termo : termosUnicos)
            arvore.delete(new ParTermoId(termo, curso.getID(), 0f));

        if (DEBUG) System.out.println("[DEBUG] Curso " + curso.getID() + " removido do índice invertido.");
    }

    // Atualiza as listas invertidas quando o nome de um curso é alterado
    public void atualizar(Curso antigo, Curso novo) throws Exception {
        remover(antigo);
        indexar(novo);
    }

    // ------------------------------------------------------------------------------
    // BUSCA POR PALAVRAS-CHAVE (TF x IDF)
    // ------------------------------------------------------------------------------

    // Busca os IDs dos cursos cujos nomes contêm os termos pesquisados,
    // ordenados pelo somatório de TF x IDF (do maior para o menor).
    public ArrayList<Integer> buscar(String texto, int totalCursos) throws Exception {
        ArrayList<String> termosBusca = Texto.extrairTermos(texto);

        // Mantém a ordem de inserção apenas como referência; a ordenação final é por pontuação
        Map<Integer, Double> pontuacoes = new LinkedHashMap<>();

        for (String termo : termosBusca) {
            ArrayList<ParTermoId> lista = arvore.read(new ParTermoId(termo, -1, 0f));
            if (lista == null || lista.isEmpty())
                continue;

            // IDF = log10(total de cursos / cursos que contêm o termo) + 1
            double idf = Math.log10((double) totalCursos / lista.size()) + 1;

            for (ParTermoId par : lista) {
                double valor = par.getTf() * idf;
                pontuacoes.merge(par.getIdCurso(), valor, Double::sum);
            }
        }

        ArrayList<Map.Entry<Integer, Double>> entradas = new ArrayList<>(pontuacoes.entrySet());
        entradas.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        ArrayList<Integer> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Double> entrada : entradas)
            resultado.add(entrada.getKey());

        return resultado;
    }

    // ------------------------------------------------------------------------------
    // FECHAMENTO
    // ------------------------------------------------------------------------------

    public void close() throws Exception {
        arvore.close();
    }
}
