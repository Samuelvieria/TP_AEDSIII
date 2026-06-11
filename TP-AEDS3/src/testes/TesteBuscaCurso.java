package testes;

import arquivos.ArquivoCurso;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Teste simples das funcionalidades de busca:
 * - Busca por código compartilhável
 * - Busca por palavras-chave (índice invertido, ordenação TFxIDF)
 */
public class TesteBuscaCurso {

    private static int contadorCodigo = 0;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("TESTE DE BUSCA DE CURSOS");
        System.out.println("=".repeat(60));

        try {
            // Prepara ambiente limpo
            limparArquivos();

            // Cria arquivos e um usuário dono dos cursos
            ArquivoUsuario arqUsuario = new ArquivoUsuario();
            ArquivoCurso arqCurso = new ArquivoCurso();

            Usuario dono = new Usuario("Professor Teste", "prof@teste.com", "1234", "Pergunta", "Resposta");
            int idDono = arqUsuario.create(dono);
            System.out.println("\n[Setup] Usuário criado com ID: " + idDono);

            // Criar cursos de exemplo (códigos com exatamente 10 caracteres)
            Curso c1 = novoCurso("Introdução à Inteligência Artificial", idDono);
            Curso c2 = novoCurso("Inteligência Emocional para Gestores", idDono);
            Curso c3 = novoCurso("Inteligência no Trabalho por Meio da Inteligência Artificial", idDono);
            Curso c4 = novoCurso("Introdução à Gestão de Equipes", idDono);

            int id1 = arqCurso.create(c1);
            int id2 = arqCurso.create(c2);
            int id3 = arqCurso.create(c3);
            int id4 = arqCurso.create(c4);

            System.out.println("\n[Setup] Cursos criados:");
            System.out.println("ID 1: " + c1.getNome() + " -> Código: " + c1.getCodigo());
            System.out.println("ID 2: " + c2.getNome() + " -> Código: " + c2.getCodigo());
            System.out.println("ID 3: " + c3.getNome() + " -> Código: " + c3.getCodigo());
            System.out.println("ID 4: " + c4.getNome() + " -> Código: " + c4.getCodigo());

            // =========================================
            // TESTE 1: Busca por código compartilhável
            // =========================================
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TESTE 1: Busca por código compartilhável");
            System.out.println("=".repeat(40));

            String codigoBusca = c1.getCodigo();
            Curso encontrado = arqCurso.readCodigo(codigoBusca);
            if (encontrado != null && encontrado.getID() == id1) {
                System.out.println("✅ Busca por código '" + codigoBusca + "' retornou o curso correto: " + encontrado.getNome());
            } else {
                System.out.println("❌ Falha: Busca por código não retornou o curso esperado.");
            }

            // Tentar código inexistente
            Curso naoEncontrado = arqCurso.readCodigo("ABCDEFGHIJ"); // 10 caracteres
            if (naoEncontrado == null) {
                System.out.println("✅ Busca por código inexistente retornou null (comportamento esperado).");
            } else {
                System.out.println("❌ Falha: Busca por código inexistente deveria retornar null.");
            }

            // =========================================
            // TESTE 2: Busca por palavras-chave (TFxIDF)
            // =========================================
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TESTE 2: Busca por palavras-chave");
            System.out.println("=".repeat(40));

            String consulta = "Inteligência Artificial";
            System.out.println("\nConsulta: \"" + consulta + "\"");
            ArrayList<Curso> resultados = arqCurso.buscarPorPalavras(consulta);

            if (resultados.isEmpty()) {
                System.out.println("❌ Nenhum resultado encontrado (esperado 3 cursos).");
            } else {
                System.out.println("Resultados encontrados: " + resultados.size());
                for (int i = 0; i < resultados.size(); i++) {
                    Curso c = resultados.get(i);
                    System.out.printf("%d. ID %d - %s%n", i+1, c.getID(), c.getNome());
                }
                // Verifica ordem esperada: ID1, ID3, ID2
                if (resultados.size() == 3 &&
                    resultados.get(0).getID() == id1 &&
                    resultados.get(1).getID() == id3 &&
                    resultados.get(2).getID() == id2) {
                    System.out.println("✅ Ordem de relevância correta (1,3,2).");
                } else {
                    System.out.println("❌ Ordem de relevância incorreta.");
                }
            }

            // Testar consulta com termo que não existe
            System.out.println("\nConsulta: \"culinária\"");
            ArrayList<Curso> vazios = arqCurso.buscarPorPalavras("culinária");
            if (vazios.isEmpty()) {
                System.out.println("✅ Busca por termo inexistente retornou lista vazia (correto).");
            } else {
                System.out.println("❌ Busca por termo inexistente deveria retornar vazio.");
            }

            // =========================================
            // TESTE 3: Atualização de curso e reindexação
            // =========================================
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TESTE 3: Atualização do nome e reindexação");
            System.out.println("=".repeat(40));

            c2.setNome("Gestão de Equipes Remotas");
            boolean atualizado = arqCurso.update(c2);
            if (!atualizado) {
                System.out.println("❌ Falha ao atualizar curso.");
            } else {
                System.out.println("Curso ID2 renomeado para: " + c2.getNome());
                // Buscar pelo termo antigo "emocional"
                ArrayList<Curso> buscaAntiga = arqCurso.buscarPorPalavras("Emocional");
                if (buscaAntiga.isEmpty()) {
                    System.out.println("✅ Termo antigo 'Emocional' não retorna mais o curso.");
                } else {
                    System.out.println("❌ Termo antigo ainda presente no índice.");
                }
                // Buscar pelo novo termo "remotas"
                ArrayList<Curso> buscaNova = arqCurso.buscarPorPalavras("Remotas");
                if (buscaNova.size() == 1 && buscaNova.get(0).getID() == id2) {
                    System.out.println("✅ Novo termo 'Remotas' indexado corretamente.");
                } else {
                    System.out.println("❌ Novo termo não foi indexado.");
                }
            }

            // =========================================
            // TESTE 4: Exclusão de curso remove do índice invertido
            // =========================================
            System.out.println("\n" + "=".repeat(40));
            System.out.println("TESTE 4: Exclusão de curso e remoção do índice");
            System.out.println("=".repeat(40));

            boolean excluido = arqCurso.delete(id4);
            if (!excluido) {
                System.out.println("❌ Falha ao excluir curso ID4.");
            } else {
                System.out.println("Curso ID4 excluído.");
                ArrayList<Curso> buscaAposExclusao = arqCurso.buscarPorPalavras("Equipes");
                // O curso ID2 (renomeado) ainda tem "equipes", então deve sobrar 1
                if (buscaAposExclusao.size() == 1 && buscaAposExclusao.get(0).getID() == id2) {
                    System.out.println("✅ Após exclusão, termo 'equipes' ainda encontra curso ID2 (correto).");
                } else {
                    System.out.println("❌ Índice invertido não foi atualizado corretamente após exclusão.");
                }
            }

            // =========================================
            // Conclusão
            // =========================================
            System.out.println("\n" + "=".repeat(60));
            System.out.println("TESTE DE BUSCA CONCLUÍDO");
            System.out.println("=".repeat(60));

            arqUsuario.close();
            arqCurso.close();

        } catch (Exception e) {
            System.err.println("Erro durante os testes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Curso novoCurso(String nome, int idUsuario) {
        contadorCodigo++;
        // Gera código com exatamente 10 caracteres (TEST + 6 dígitos)
        String codigo = String.format("TEST%06d", contadorCodigo);
        return new Curso(nome, "Descrição automática", LocalDate.of(2026, 1, 1), codigo, (byte) 0, idUsuario);
    }

    private static void limparArquivos() {
        String[] caminhos = {
            "dados/cursos", "dados/usuarios",
            "dados/cursos_codigo.idx", "dados/cursos_codigo.dir",
            "dados/cursos_nome.db", "dados/cursos_usuario.db",
            "dados/cursos_invertido.db", "dados/usuarios_email.idx",
            "dados/usuarios_email.dir"
        };
        for (String caminho : caminhos) {
            java.io.File f = new java.io.File(caminho);
            if (f.exists()) {
                if (f.isDirectory()) {
                    java.io.File[] filhos = f.listFiles();
                    if (filhos != null)
                        for (java.io.File filho : filhos) filho.delete();
                }
                f.delete();
            }
        }
        System.out.println("[Limpeza] Arquivos antigos removidos.");
    }
}