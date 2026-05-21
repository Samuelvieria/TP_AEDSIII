package testes;

import arquivos.ArquivoInscricao;
import entidades.Inscricao;
import java.util.ArrayList;
import java.io.File;

public class TesteInscricao {
    public static void main(String[] args) {
        try {
            // Limpa os arquivos anteriores
            limparArquivos();

            ArquivoInscricao arq = new ArquivoInscricao();
            System.out.println("--- INICIANDO TESTE DA PARTE A ---");

            // 1. TESTE DE VOLUME (Forçar divisões na Árvore B+)
            System.out.println("1. Testando inserção de 100 inscrições...");
            for (int i = 1; i <= 100; i++) {
                arq.create(new Inscricao(1, i)); // Usuário 1 em 100 cursos diferentes
            }

            ArrayList<Inscricao> listaU1 = arq.listarPorUsuario(1);
            System.out.println("   -> Total recuperado para Usuário 1: " + listaU1.size() + " (Esperado: 100)");

            // 2. TESTE DE INTEGRIDADE DE EXCLUSÃO
            System.out.println("2. Testando exclusão...");
            // Vamos deletar a inscrição ID 50
            boolean deletou = arq.delete(50);
            ArrayList<Inscricao> listaU1PosDelete = arq.listarPorUsuario(1);
            System.out.println("   -> Deletou ID 50? " + deletou);
            System.out.println("   -> Total após delete: " + listaU1PosDelete.size() + " (Esperado: 99)");

            // 3. TESTE DE BUSCA POR CURSO (O outro lado do N:N)
            System.out.println("3. Testando busca por curso...");
            // Criar vários usuários no mesmo curso (Curso 999)
            arq.create(new Inscricao(10, 999));
            arq.create(new Inscricao(20, 999));
            arq.create(new Inscricao(30, 999));

            ArrayList<Inscricao> listaC999 = arq.listarPorCurso(999);
            System.out.println("   -> Alunos no curso 999: " + listaC999.size() + " (Esperado: 3)");

            // 4. TESTE DE CONSISTÊNCIA (IDs inexistentes)
            System.out.println("4. Testando busca por ID inexistente...");
            ArrayList<Inscricao> listaVazia = arq.listarPorUsuario(8888);
            System.out.println("   -> Resultado para usuário fantasma: " + listaVazia.size() + " (Esperado: 0)");

            // 5. TESTE DE DUPLICATA
            System.out.println("5. Testando tentativa de inscrição duplicada...");
            try {
                arq.create(new Inscricao(10, 999)); // Usuário 10 no curso 999 de novo
                System.out.println("   -> ERRO: O sistema permitiu duplicata!");
            } catch (Exception e) {
                System.out.println("   -> SUCESSO: O sistema barrou a duplicata. Mensagem: " + e.getMessage());
            }

            arq.close();
            System.out.println("\n--- TESTE FINALIZADO COM SUCESSO ---");

        } catch (Exception e) {
            System.err.println("\n[ERRO DETECTADO]: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void limparArquivos() {
        String[] arquivos = {
                "dados/inscricoes.db",
                "dados/inscricoes_usuario.db",
                "dados/inscricoes_curso.db"
        };
        for (String s : arquivos) {
            File f = new File(s);
            if (f.exists())
                f.delete();
        }
    }
}