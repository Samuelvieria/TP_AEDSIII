package indices;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

// Funções utilitárias de tratamento de texto usadas pelo índice invertido:
// normalização (minúsculas e sem acentos) e extração de termos válidos para indexação.
public class Texto {

    private static final Pattern DIACRITICOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final Pattern SEPARADORES = Pattern.compile("[^a-z0-9]+");
    private static final Pattern SOMENTE_NUMEROS = Pattern.compile("\\d+");

    // Remove acentos e converte para minúsculas
    public static String normalizar(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return DIACRITICOS.matcher(semAcentos).replaceAll("").toLowerCase();
    }

    // Quebra o texto em palavras, normaliza, remove stop words e numerais
    // Exemplo: "Introdução à Inteligência Artificial" -> [introducao, inteligencia, artificial]
    public static ArrayList<String> extrairTermos(String texto) {
        ArrayList<String> termos = new ArrayList<>();
        if (texto == null || texto.isEmpty())
            return termos;

        String normalizado = normalizar(texto);
        String[] palavras = SEPARADORES.split(normalizado);

        for (String palavra : palavras) {
            if (palavra.isEmpty())
                continue;
            if (SOMENTE_NUMEROS.matcher(palavra).matches())
                continue;
            if (StopWords.isStopWord(palavra))
                continue;
            termos.add(palavra);
        }

        return termos;
    }
}
