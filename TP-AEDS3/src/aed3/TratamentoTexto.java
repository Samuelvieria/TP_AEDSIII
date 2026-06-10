package aed3;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe responsável pelo tratamento e normalização de texto para o índice invertido.
 * Remove acentos, stop words, e converte para minúsculas.
 */
public class TratamentoTexto {
    
    // Lista de stop words em português
    private static final Set<String> STOP_WORDS = new HashSet<>();
    
    static {
        // Artigos
        STOP_WORDS.add("a");
        STOP_WORDS.add("o");
        STOP_WORDS.add("um");
        STOP_WORDS.add("uma");
        STOP_WORDS.add("as");
        STOP_WORDS.add("os");
        STOP_WORDS.add("uns");
        STOP_WORDS.add("umas");
        
        // Preposições
        STOP_WORDS.add("de");
        STOP_WORDS.add("para");
        STOP_WORDS.add("com");
        STOP_WORDS.add("do");
        STOP_WORDS.add("da");
        STOP_WORDS.add("em");
        STOP_WORDS.add("por");
        STOP_WORDS.add("na");
        STOP_WORDS.add("no");
        STOP_WORDS.add("à");
        STOP_WORDS.add("ao");
        STOP_WORDS.add("dos");
        STOP_WORDS.add("das");
        STOP_WORDS.add("pela");
        STOP_WORDS.add("pelo");
        STOP_WORDS.add("nas");
        STOP_WORDS.add("nos");
        STOP_WORDS.add("entre");
        STOP_WORDS.add("sem");
        STOP_WORDS.add("sob");
        STOP_WORDS.add("sobre");
        STOP_WORDS.add("até");
        STOP_WORDS.add("após");
        STOP_WORDS.add("antes");
        STOP_WORDS.add("durante");
        STOP_WORDS.add("contra");
        STOP_WORDS.add("perante");
        
        // Conjunções
        STOP_WORDS.add("e");
        STOP_WORDS.add("mas");
        STOP_WORDS.add("porém");
        STOP_WORDS.add("contudo");
        STOP_WORDS.add("todavia");
        STOP_WORDS.add("entretanto");
        STOP_WORDS.add("ou");
        STOP_WORDS.add("nem");
        STOP_WORDS.add("se");
        STOP_WORDS.add("quando");
        STOP_WORDS.add("onde");
        STOP_WORDS.add("como");
        
        // Pronomes
        STOP_WORDS.add("que");
        STOP_WORDS.add("qual");
        STOP_WORDS.add("quais");
        STOP_WORDS.add("quanto");
        STOP_WORDS.add("quantos");
        STOP_WORDS.add("quantas");
        
        // Outros
        STOP_WORDS.add("etc");
        STOP_WORDS.add("menos");
        STOP_WORDS.add("exceto");
        STOP_WORDS.add("salvo");
        STOP_WORDS.add("tirante");
        STOP_WORDS.add("inclusive");
        STOP_WORDS.add("conforme");
        STOP_WORDS.add("segundo");
        STOP_WORDS.add("consoante");
    }

    /**
     * Remove acentos e diacríticos de uma string.
     * Utiliza normalização Unicode NFD + remoção de marcas diacríticas.
     */
    public static String removerAcentos(String texto) {
        if (texto == null)
            return "";
        
        // Normalizar para NFD (decomposição)
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        
        // Remover marcas diacríticas (acentos, til, etc.)
        return nfd.replaceAll("\\p{M}", "");
    }

    /**
     * Tokeniza uma string em palavras separadas por espaços e pontuação.
     */
    public static List<String> tokenizar(String texto) {
        List<String> tokens = new ArrayList<>();
        
        if (texto == null || texto.trim().isEmpty())
            return tokens;
        
        // Substituir pontuação por espaço
        String limpo = texto.replaceAll("[^a-zA-Z0-9\\s]", " ");
        
        // Dividir por espaços
        String[] palavras = limpo.split("\\s+");
        
        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                tokens.add(palavra);
            }
        }
        
        return tokens;
    }

    /**
     * Verifica se uma palavra é um stop word.
     */
    public static boolean ehStopWord(String palavra) {
        if (palavra == null)
            return true;
        return STOP_WORDS.contains(palavra.toLowerCase());
    }

    /**
     * Normaliza uma string removendo acentos e convertendo para minúsculas.
     */
    public static String normalizar(String texto) {
        if (texto == null)
            return "";
        
        // Remover acentos
        String semAcentos = removerAcentos(texto);
        
        // Converter para minúsculas
        return semAcentos.toLowerCase().trim();
    }

    /**
     * Processa um texto completo extraindo termos válidos (sem stop words, normalizados).
     * Retorna uma lista de termos prontos para indexação.
     */
    public static List<String> extrairTermos(String texto) {
        List<String> termos = new ArrayList<>();
        
        if (texto == null || texto.trim().isEmpty())
            return termos;
        
        // Tokenizar
        List<String> tokens = tokenizar(texto);
        
        // Processar cada token
        for (String token : tokens) {
            // Normalizar
            String normalizado = normalizar(token);
            
            // Verificar se não é vazio e não é stop word
            if (!normalizado.isEmpty() && !ehStopWord(normalizado)) {
                termos.add(normalizado);
            }
        }
        
        return termos;
    }

    /**
     * Calcula o TF (Term Frequency) de um termo em um documento.
     * TF = frequência do termo / total de termos válidos
     */
    public static float calcularTF(String texto, String termo) {
        if (texto == null || termo == null || termo.trim().isEmpty())
            return 0.0f;
        
        List<String> termos = extrairTermos(texto);
        
        if (termos.isEmpty())
            return 0.0f;
        
        String termoNormalizado = normalizar(termo);
        int frequencia = 0;
        
        for (String t : termos) {
            if (t.equals(termoNormalizado)) {
                frequencia++;
            }
        }
        
        return (float) frequencia / termos.size();
    }

    /**
     * Calcula o IDF (Inverse Document Frequency) de um termo.
     * IDF = log10(N / n_t) + 1
     * Onde N = total de documentos, n_t = número de documentos com o termo
     */
    public static float calcularIDF(int totalDocumentos, int documentosComTermo) {
        if (totalDocumentos <= 0 || documentosComTermo <= 0)
            return 0.0f;
        
        return (float) (Math.log10((double) totalDocumentos / documentosComTermo) + 1);
    }

    /**
     * Calcula o score TFxIDF de um termo em um documento.
     * TFxIDF = TF * IDF
     */
    public static float calcularTFxIDF(float tf, float idf) {
        return tf * idf;
    }
}
