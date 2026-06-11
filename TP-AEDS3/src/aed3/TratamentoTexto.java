package aed3;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
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
        Collections.addAll(STOP_WORDS,
                "a", "o", "um", "uma", "as", "os", "uns", "umas");
        
        // Preposições
        Collections.addAll(STOP_WORDS,
                "de", "para", "com", "do", "da", "em", "por", "na", "no",
                "ao", "aos", "dos", "das", "pela", "pelas", "pelo", "pelos",
                "nas", "nos", "entre", "sem", "sob", "sobre", "ate", "apos",
                "antes", "depois", "durante", "contra", "perante");
        
        // Conjunções
        Collections.addAll(STOP_WORDS,
                "e", "mas", "porem", "contudo", "todavia", "entretanto",
                "ou", "nem", "se", "quando", "onde", "como");
        
        // Pronomes
        Collections.addAll(STOP_WORDS,
                "que", "qual", "quais", "quanto", "quantos", "quanta",
                "quantas", "quem", "cujo", "cuja", "cujos", "cujas");

        // Numerais por extenso mais comuns
        Collections.addAll(STOP_WORDS,
                "zero", "um", "uma", "dois", "duas", "tres", "quatro",
                "cinco", "seis", "sete", "oito", "nove", "dez", "primeiro",
                "primeira", "segundo", "segunda", "terceiro", "terceira");
        
        // Outros
        Collections.addAll(STOP_WORDS,
                "etc", "menos", "exceto", "salvo", "tirante", "inclusive",
                "conforme", "segundo", "consoante");
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
        
        // Normaliza antes da regex para não quebrar palavras com acentos.
        String normalizado = normalizar(texto);

        // Substituir pontuação por espaço
        String limpo = normalizado.replaceAll("[^a-z0-9\\s]", " ");
        
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

        String normalizada = normalizar(palavra);
        return STOP_WORDS.contains(normalizada);
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
            
            // Verificar se não é vazio, numeral puro ou stop word
            if (!normalizado.isEmpty() && !normalizado.matches("\\d+") && !ehStopWord(normalizado)) {
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
