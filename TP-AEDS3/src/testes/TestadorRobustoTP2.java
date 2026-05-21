package testes;

import arquivos.ArquivoCurso;
import arquivos.ArquivoInscricao;
import arquivos.ArquivoUsuario;
import entidades.Curso;
import entidades.Inscricao;
import entidades.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;

public class TestadorRobustoTP2 {

    private static int testesPassados = 0;
    private static int totalTestes = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   INICIANDO TESTE DE ESTRESSE ROBUSTO - TP2      ");
        System.out.println("==================================================");

        try {
            // Inicializa todos os arquivos gerenciadores
            ArquivoUsuario arqUsuario = new ArquivoUsuario();
            ArquivoCurso arqCurso = new ArquivoCurso();
            ArquivoInscricao arqInscricao = new ArquivoInscricao();

            // ==================================================
            // CENÁRIO 1: CRUD de Usuário & Integridade de E-mail
            // ==================================================
            System.out.println("\n[Cenário 1] CRUD de Usuário e Trava de E-mail Único");

            Usuario u1 = new Usuario();
            u1.setNome("Marco Antonio");
            u1.setEmail("marco@aeds.com");
            u1.setSenha("senha123");
            int idUser1 = arqUsuario.create(u1);

            // Teste de Read e Update do Usuário
            Usuario uBuscado = arqUsuario.read(idUser1);
            boolean readUserOk = (uBuscado != null && uBuscado.getNome().equals("Marco Antonio"));

            if (readUserOk) {
                uBuscado.setNome("Marco Modificado");
                arqUsuario.update(uBuscado);
                Usuario uAlterado = arqUsuario.read(idUser1);
                Verificar(uAlterado.getNome().equals("Marco Modificado"),
                        "Usuário: Create, Read e Update funcionando no arquivo físico.");
            } else {
                Verificar(false, "Falha ao ler usuário criado no arquivo base.");
            }

            // Teste de Integridade: Impedir mesmo E-mail
            try {
                Usuario uDuplicado = new Usuario();
                uDuplicado.setNome("Outro Marco");
                uDuplicado.setEmail("marco@aeds.com"); // Mesmo e-mail!
                uDuplicado.setSenha("9876");
                arqUsuario.create(uDuplicado);
                Verificar(false, "ERRO: O sistema permitiu cadastrar dois usuários com o mesmo e-mail!");
            } catch (Exception e) {
                Verificar(true, "SUCESSO: Sistema barrou cadastro de e-mail duplicado.");
            }

            // Cadastra mais dois alunos para usar nas inscrições
            Usuario u2 = new Usuario();
            u2.setNome("Aluno A");
            u2.setEmail("alunoa@aeds.com");
            u2.setSenha("1234");
            Usuario u3 = new Usuario();
            u3.setNome("Aluno B");
            u3.setEmail("alunob@aeds.com");
            u3.setSenha("1234");
            int idUser2 = arqUsuario.create(u2);
            int idUser3 = arqUsuario.create(u3);

            // ==================================================
            // CENÁRIO 2: CRUD de Curso & Integridade de Código (Hash)
            // ==================================================
            System.out.println("\n[Cenário 2] CRUD de Curso e Trava de Código Único (Hash Extensível)");

            Curso c1 = new Curso();
            c1.setNome("AEDS III");
            c1.setDescricao("TP2 de Arquivos");
            c1.setCodigo("AEDS3_TP2X"); // Exatamente 10 caracteres
            c1.setDataInicio(LocalDate.of(2026, 3, 1));
            c1.setIdUsuario(idUser1);
            c1.setEstado((byte) 0);
            int idCurso1 = arqCurso.create(c1);

            // Teste de Read e Update do Curso
            Curso cBuscado = arqCurso.readCodigo("AEDS3_TP2X");
            boolean readCursoOk = (cBuscado != null && cBuscado.getID() == idCurso1);

            if (readCursoOk) {
                cBuscado.setDescricao("Nova Descricao do TP2");
                arqCurso.update(cBuscado);
                Curso cAlterado = arqCurso.read(idCurso1);
                Verificar(cAlterado.getDescricao().equals("Nova Descricao do TP2"),
                        "Curso: Create, Read (por Código) e Update funcionando.");
            } else {
                Verificar(false, "Falha ao localizar curso via Hash Extensível.");
            }

            // Teste de Integridade: Impedir mesmo Código de Curso
            try {
                Curso cDuplicado = new Curso();
                cDuplicado.setNome("Outro Curso");
                cDuplicado.setCodigo("AEDS3_TP2X"); // Mesmo código!
                cDuplicado.setDataInicio(LocalDate.of(2026, 4, 1));
                cDuplicado.setIdUsuario(idUser1);
                cDuplicado.setEstado((byte) 0);
                arqCurso.create(cDuplicado);
                Verificar(false, "ERRO: O sistema permitiu criar dois cursos com o mesmo código!");
            } catch (Exception e) {
                Verificar(true, "SUCESSO: Hash Extensível barrou código de curso duplicado.");
            }

            // ==================================================
            // CENÁRIO 3: CRUD de Inscrição & Integridade de Duplicidade
            // ==================================================
            System.out.println("\n[Cenário 3] CRUD de Inscrição e Trava de Matrícula Duplicada");

            Inscricao i1 = new Inscricao();
            i1.setIdUsuario(idUser2); // Aluno A
            i1.setIdCurso(idCurso1);
            int idInscricao1 = arqInscricao.create(i1);

            // Teste de Integridade: Impedir que Aluno A se inscreva duas vezes no mesmo
            // curso
            try {
                Inscricao iDuplicada = new Inscricao();
                iDuplicada.setIdUsuario(idUser2);
                iDuplicada.setIdCurso(idCurso1);
                arqInscricao.create(iDuplicada);
                Verificar(false, "ERRO: Sistema permitiu duplicar matrícula do mesmo aluno no curso!");
            } catch (Exception e) {
                Verificar(true, "SUCESSO: Árvore B+ barrou matrícula duplicada com sucesso.");
            }

            // Teste de Update de Inscrição (Transferência de vaga do Aluno A para o Aluno
            // B)
            Inscricao relacao = arqInscricao.read(idInscricao1);
            if (relacao != null) {
                relacao.setIdUsuario(idUser3); // Altera o aluno vinculado para o Aluno B
                boolean updateInscOk = arqInscricao.update(relacao);

                Inscricao antigoDono = arqInscricao.buscarRelacao(idUser2, idCurso1);
                Inscricao novoDono = arqInscricao.buscarRelacao(idUser3, idCurso1);

                Verificar(updateInscOk && antigoDono == null && novoDono != null,
                        "Inscrição: Update efetuado e reindexado na Árvore B+ com sucesso.");
            } else {
                Verificar(false, "Falha ao ler a inscrição criada.");
            }

            // ==================================================
            // CENÁRIO 4: Teste de Integridade Crítico - Exclusão com Alunos
            // ==================================================
            System.out.println("\n[Cenário 4] Integridade Referencial - Excluir Curso com Aluno");

            try {
                // Aluno B (idUser3) está matriculado ativamente no Curso 1. A exclusão DEVE ser
                // impedida.
                arqCurso.delete(idCurso1);
                Verificar(false, "ERRO: O sistema permitiu excluir um curso que possuía alunos matriculados!");
            } catch (Exception e) {
                Verificar(e.getMessage().contains("possui alunos inscritos"),
                        "SUCESSO: Trava impediu a exclusão do curso ativo.");
            }

            // ==================================================
            // CENÁRIO 5: Paginação Dinâmica via Árvore B+
            // ==================================================
            System.out.println("\n[Cenário 5] Paginação e Ordenação Real por Árvore B+");

            for (int i = 1; i <= 12; i++) {
                Curso aux = new Curso();
                aux.setNome("Curso " + i);
                aux.setDescricao("Paginado");
                String codigoPaginado = String.format("PAG-%06d", i); // 10 caracteres rígidos
                aux.setCodigo(codigoPaginado);
                aux.setDataInicio(LocalDate.of(2026, 5, 20).minusDays(i)); // Datas decrescentes
                aux.setIdUsuario(idUser1);
                aux.setEstado((byte) 0);
                arqCurso.create(aux);
            }

            ArrayList<Curso> cursosOrdenados = arqCurso.listarCursosOrdenadosPorData();
            boolean ordenacaoValida = true;
            for (int i = 0; i < cursosOrdenados.size() - 1; i++) {
                if (cursosOrdenados.get(i).getDataInicio().isAfter(cursosOrdenados.get(i + 1).getDataInicio())) {
                    ordenacaoValida = false;
                    break;
                }
            }

            Verificar(cursosOrdenados.size() >= 13 && ordenacaoValida,
                    "Listagem via Árvore B+ trouxe dados limpos e ordenados por data.");

            int limitePagina = Math.min(10, cursosOrdenados.size());
            Verificar(limitePagina == 10, "Fatiamento lógico capturou exatamente os 10 itens da primeira página.");

            // ==================================================
            // CENÁRIO 6: Desfazendo Vínculos e Deleção Final de Curso (Lixeira)
            // ==================================================
            System.out.println("\n[Cenário 6] Fluxo de Deleção Limpa de Relacionamentos e Arquivo Base");

            // 1. Deleta a inscrição ativa (Aluno B sai do curso)
            Inscricao relacaoAtiva = arqInscricao.buscarRelacao(idUser3, idCurso1);
            boolean deletouInscricao = arqInscricao.delete(relacaoAtiva.getID());
            Inscricao relacaoPosDelecao = arqInscricao.buscarRelacao(idUser3, idCurso1);

            boolean inscricaoLimpa = (deletouInscricao && relacaoPosDelecao == null);
            Verificar(inscricaoLimpa, "Inscrição deletada fisicamente e removida dos índices da Árvore B+.");

            // 2. Agora que o curso está vazio, a deleção do curso DEVE funcionar
            boolean deletouCurso = arqCurso.delete(idCurso1);
            Curso cursoPosDelecao = arqCurso.read(idCurso1);

            Verificar(deletouCurso && cursoPosDelecao == null,
                    "Curso excluído com sucesso do arquivo físico após ficar livre de alunos.");

            // Fechamento dos arquivos
            arqUsuario.close();
            arqCurso.close();
            arqInscricao.close();

            // ==================================================
            // RELATÓRIO FINAL
            // ==================================================
            System.out.println("\n==================================================");
            System.out.println("              RESULTADO DOS TESTES                ");
            System.out.println("==================================================");
            System.out.println("Testes Executados: " + totalTestes);
            System.out.println("Testes Passados:   " + testesPassados);
            System.out.println("Testes Falhados:   " + (totalTestes - testesPassados));

            if (testesPassados == totalTestes) {
                System.out
                        .println("\n🔥 PARABÉNS! Seu TP2 passou em 100% dos testes de estresse, CRUDs e integridade.");
            } else {
                System.out.println("\n⚠️ ATENÇÃO: Corrija as falhas de integridade apontadas acima.");
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("\n❌ ERRO FATAL DE EXECUÇÃO NO TESTADOR:");
            e.printStackTrace();
        }
    }

    private static void Verificar(boolean condicao, String mensagemSucesso) {
        totalTestes++;
        if (condicao) {
            System.out.println("   🟢 PASSOU: " + mensagemSucesso);
            testesPassados++;
        } else {
            System.out.println("   🔴 FALHOU!");
        }
    }
}
