package indices;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Lista de palavras vazias (stop words) em português, usada para descartar
// artigos, preposições, conjunções, pronomes e numerais na criação do índice invertido.
public class StopWords {

    // As palavras já estão em letras minúsculas e sem acentos, no mesmo
    // formato gerado por Texto.normalizar()
    private static final Set<String> PALAVRAS = new HashSet<>(Arrays.asList(
            "a", "as", "o", "os", "um", "uma", "uns", "umas",
            "de", "do", "da", "dos", "das", "em", "no", "na", "nos", "nas",
            "num", "numa", "nuns", "numas", "por", "pelo", "pela", "pelos", "pelas",
            "para", "com", "sem", "sob", "sobre", "entre", "ate", "apos", "ante", "perante", "desde", "durante",
            "e", "ou", "mas", "se", "que", "quando", "como", "porque", "pois",
            "embora", "contudo", "todavia", "porem", "entao", "assim", "tambem",
            "ja", "nao", "sim", "mais", "menos", "muito", "muitos", "muita", "muitas",
            "pouco", "poucos", "pouca", "poucas",
            "este", "esta", "estes", "estas", "esse", "essa", "esses", "essas",
            "aquele", "aquela", "aqueles", "aquelas", "isto", "isso", "aquilo",
            "eu", "tu", "ele", "ela", "nos", "vos", "eles", "elas",
            "me", "te", "lhe", "lhes",
            "meu", "minha", "meus", "minhas", "teu", "tua", "teus", "tuas",
            "seu", "sua", "seus", "suas", "nosso", "nossa", "nossos", "nossas",
            "ao", "aos", "a", "as",
            "qual", "quais", "quem", "cujo", "cuja", "cujos", "cujas",
            "ser", "estar", "ter", "haver", "fazer",
            "um", "dois", "duas", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez"));

    // Verifica se a palavra (já normalizada) é uma stop word
    public static boolean isStopWord(String palavra) {
        return PALAVRAS.contains(palavra);
    }
}
