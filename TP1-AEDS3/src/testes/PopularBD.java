package testes;

import arquivos.ArquivoCurso;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Usuario;

import java.time.LocalDate;

// CLASSE AUXILIAR PARA POPULAR O BANCO DE DADOS COM DADOS DE TESTE
// Útil para demonstrações e validação rápida das funcionalidades.
public class PopularBD {

    private static final boolean DEBUG = false;

    // Popula usuários e cursos de exemplo.
    // Retorna true se tudo foi inserido com sucesso.
    public static boolean executar() {
        if (DEBUG) System.out.println("[DEBUG] Iniciando população do BD...");

        ArquivoUsuario arqUsuario = null;
        ArquivoCurso arqCurso = null;

        try {
            arqUsuario = new ArquivoUsuario();
            arqCurso = new ArquivoCurso();

            // ----- CRIAÇÃO DE USUÁRIOS -----
            Usuario u1 = new Usuario("João Silva", "joao@email.com", "1234", "Cidade natal?", "BH");
            Usuario u2 = new Usuario("Maria Oliveira", "maria@email.com", "abcd", "Nome do pet?", "Rex");
            Usuario u3 = new Usuario("Carlos Souza", "carlos@email.com", "senha123", "Cor favorita?", "Azul");

            int idJoao = arqUsuario.create(u1);
            int idMaria = arqUsuario.create(u2);
            int idCarlos = arqUsuario.create(u3);

            if (DEBUG) System.out.println("[DEBUG] Usuários criados: João=" + idJoao + ", Maria=" + idMaria + ", Carlos=" + idCarlos);

            // ----- CRIAÇÃO DE CURSOS (vinculados aos usuários) -----
            // Cursos do João
            Curso c1 = new Curso("Finanças Pessoais", "Aprenda a controlar seu dinheiro",
                    LocalDate.of(2026, 2, 10), gerarCodigo(), (byte) 0, idJoao);
            Curso c2 = new Curso("Python Básico", "Introdução à programação com Python",
                    LocalDate.of(2026, 3, 15), gerarCodigo(), (byte) 0, idJoao);

            // Cursos da Maria
            Curso c3 = new Curso("Fotografia Digital", "Técnicas de composição e edição",
                    LocalDate.of(2026, 4, 20), gerarCodigo(), (byte) 0, idMaria);
            Curso c4 = new Curso("Inglês Instrumental", "Leitura e conversação para negócios",
                    LocalDate.of(2026, 5, 5), gerarCodigo(), (byte) 0, idMaria);

            // Curso do Carlos
            Curso c5 = new Curso("Javascript para Iniciantes", "Do zero ao primeiro script",
                    LocalDate.of(2026, 6, 1), gerarCodigo(), (byte) 0, idCarlos);

            arqCurso.create(c1);
            arqCurso.create(c2);
            arqCurso.create(c3);
            arqCurso.create(c4);
            arqCurso.create(c5);

            if (DEBUG) System.out.println("[DEBUG] 5 cursos criados e vinculados aos usuários.");

            System.out.println("\n>>> Banco de dados populado com sucesso!");
            System.out.println("    Usuários: joao@email.com / 1234, maria@email.com / abcd, carlos@email.com / senha123");
            System.out.println("    Cursos vinculados a cada usuário.\n");

            return true;

        } catch (Exception e) {
            System.err.println("Erro ao popular BD: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (arqUsuario != null) arqUsuario.close();
                if (arqCurso != null) arqCurso.close();
            } catch (Exception e) { /* ignora */ }
        }
    }

    // Gera um código aleatório de 10 caracteres (estilo NanoID)
    private static String gerarCodigo() {
        String alfabeto = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int indice = (int) (Math.random() * alfabeto.length());
            sb.append(alfabeto.charAt(indice));
        }
        return sb.toString();
    }
}