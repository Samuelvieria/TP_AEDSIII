package testes;

import arquivos.ArquivoCurso;
import arquivos.ArquivoInscricao;
import entidades.Curso;
import entidades.Inscricao;
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
            // Inicializa os arquivos de dados
            ArquivoCurso arqCurso = new ArquivoCurso();
            ArquivoInscricao arqInscricao = new ArquivoInscricao();

            // Limpeza inicial rápida simulada por inserções controladas
            // (Para um teste limpo isolado, ideal rodar com os arquivos .db apagados)

            // ==================================================
            // CENÁRIO 1: Cadastro e Indexação Hash Extensível
            // ==================================================
            IniciarCenario("Busca exata por Código (Hash Extensível)");
            Curso c1 = new Curso();
            c1.setNome("AEDS III"); // Apenas 8 caracteres, passa fácil!
            c1.setDescricao("TP2 de Arquivos e Índices");
            c1.setCodigo("AEDS3_TP2X");
            c1.setDataInicio(LocalDate.of(2026, 3, 1));
            c1.setIdUsuario(1); // Professor/Proponente ID 1
            c1.setEstado((byte) 0); // Ativo / Aberto com cast explícito

            int idCurso1 = arqCurso.create(c1);

            Curso cBuscado = arqCurso.readCodigo("AEDS3_TP2X"); // Busca dinamicamente o código gerado
            Verificar(cBuscado != null && cBuscado.getID() == idCurso1,
                    "Curso indexado e localizado via Hash Extensível com sucesso.");

            // ==================================================
            // CENÁRIO 2: Impedir Inscrição Duplicada (Árvore B+)
            // ==================================================
            IniciarCenario("Bloqueio de Matrícula Duplicada no mesmo Curso");
            Inscricao i1 = new Inscricao();
            i1.setIdUsuario(2); // Aluno ID 2
            i1.setIdCurso(idCurso1);
            arqInscricao.create(i1); // Primeira inscrição deve passar

            try {
                Inscricao i2 = new Inscricao();
                i2.setIdUsuario(2); // Aluno ID 2 tentando se inscrever de novo
                i2.setIdCurso(idCurso1);
                arqInscricao.create(i2);
                Verificar(false, "ERRO: O sistema permitiu uma inscrição duplicada!");
            } catch (Exception e) {
                Verificar(e.getMessage().contains("já está inscrito"),
                        "SUCESSO: Bloqueio de duplicidade via Árvore B+ funcionando.");
            }

            // ==================================================
            // CENÁRIO 3: Atualização de Relacionamentos (CRUD Update)
            // ==================================================
            IniciarCenario("CRUD Update de Inscrições com Mudança de Índices");
            Inscricao relacao = arqInscricao.buscarRelacao(2, idCurso1);
            if (relacao != null) {
                relacao.setIdUsuario(3); // Transferindo a inscrição do Usuário 2 para o Usuário 3
                boolean atualizou = arqInscricao.update(relacao);

                Inscricao antigoDono = arqInscricao.buscarRelacao(2, idCurso1);
                Inscricao novoDono = arqInscricao.buscarRelacao(3, idCurso1);

                Verificar(atualizou && antigoDono == null && novoDono != null,
                        "Inscrição atualizada e reindexada na Árvore B+ com sucesso.");
            } else {
                Verificar(false, "Falha ao recuperar relação para update.");
            }

            // ==================================================
            // CENÁRIO 4: Integridade Referencial na Deleção
            // ==================================================
            IniciarCenario("Impedir Exclusão de Curso com Alunos Matriculados");
            try {
                arqCurso.delete(idCurso1); // Usuário 3 está matriculado nele, não pode deletar!
                Verificar(false, "ERRO: O sistema permitiu apagar um curso que possui alunos ativos!");
            } catch (Exception e) {
                Verificar(e.getMessage().contains("possui alunos inscritos"),
                        "SUCESSO: Trava de segurança impediu a deleção do curso.");
            }

            // ==================================================
            // CENÁRIO 5: Paginação Eficiente de Dados (Estresse)
            // ==================================================
            IniciarCenario("Paginação e Ordenação por Árvore B+ (Sem chutar IDs)");
            // Vamos cadastrar mais 12 cursos para forçar a paginação (totalizando 13)
            for (int i = 1; i <= 12; i++) {
                Curso aux = new Curso();
                aux.setNome("Curso " + i);
                aux.setDescricao("Gerado automaticamente");

                // CORREÇÃO: Força o código a ter exatamente 10 caracteres usando formatação em
                // string
                // Exemplo para i = 5: "PAG-000005" (exatamente 10 caracteres)
                String codigoPaginado = String.format("PAG-%06d", i);
                aux.setCodigo(codigoPaginado);

                aux.setDataInicio(LocalDate.of(2026, 5, 20).minusDays(i)); // Datas diferentes decrescentes
                aux.setIdUsuario(1);
                aux.setEstado((byte) 0);
                arqCurso.create(aux);
            }

            // Testa a listagem ordenada por data vinda da Árvore B+
            ArrayList<Curso> cursosOrdenados = arqCurso.listarCursosOrdenadosPorData();
            boolean ordenacaoCorreta = true;
            for (int i = 0; i < cursosOrdenados.size() - 1; i++) {
                if (cursosOrdenados.get(i).getDataInicio().isAfter(cursosOrdenados.get(i + 1).getDataInicio())) {
                    ordenacaoCorreta = false;
                    break;
                }
            }

            Verificar(cursosOrdenados.size() >= 13 && ordenacaoCorreta,
                    "Listagem via Árvore B+ trouxe os registros ativos ordenados por data sem chutar IDs.");

            // Simulação de limite de página (10 por página)
            int itensPagina1 = Math.min(10, cursosOrdenados.size());
            Verificar(itensPagina1 == 10, "Cálculo matemático de fatiamento de página isolou exatamente 10 itens.");

            // ==================================================
            // CENÁRIO 6: Remoção Limpa e Reaproveitamento de Espaço
            // ==================================================
            IniciarCenario("Cancelamento de Inscrição (Delete) e Limpeza de Índices");
            Inscricao relacaoAtiva = arqInscricao.buscarRelacao(3, idCurso1);
            boolean deletouInscricao = arqInscricao.delete(relacaoAtiva.getID());

            Inscricao relacaoPosDelecao = arqInscricao.buscarRelacao(3, idCurso1);
            Verificar(deletouInscricao && relacaoPosDelecao == null,
                    "Inscrição cancelada e removida cirurgicamente das folhas da Árvore B+.");

            // Fechamento seguro dos arquivos
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
                System.out.println("\n🔥 PARABÉNS! Seu TP2 passou em 100% dos testes de estresse e integridade.");
            } else {
                System.out.println("\n⚠️ ATENÇÃO: Corrija as falhas apontadas acima antes de enviar o trabalho.");
            }
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("\n❌ ERRO FATAL DE EXECUÇÃO NO TESTADOR:");
            e.printStackTrace();
        }
    }

    private static void IniciarCenario(String nomeCenario) {
        System.out.println("\n" + nomeCenario);
    }

    private static void Verificar(boolean condicao, String mensagemSucesso) {
        totalTestes++; // Agora contamos cada verificação individualmente
        if (condicao) {
            System.out.println("   🟢 PASSOU: " + mensagemSucesso);
            testesPassados++;
        } else {
            System.out.println("   🔴 FALHOU!");
        }
    }
}