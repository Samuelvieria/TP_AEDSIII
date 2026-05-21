package arquivos;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.HashExtensivel;
import entidades.Curso;
import entidades.Inscricao;
import indices.ParCodigoId;
import indices.ParNomeCursoId;
import indices.ParUsuarioCursoId;
import java.util.ArrayList;

public class ArquivoCurso extends Arquivo<Curso> {

    // Índices para buscas rápidas:
    // indiceCodigo: busca exata por código do curso (hash extensível)
    // indiceNome: busca por nome do curso (árvore B+)
    // indiceUsuario: busca todos os cursos de um usuário (árvore B+)

    private HashExtensivel<ParCodigoId> indiceCodigo;
    private ArvoreBMais<ParNomeCursoId> indiceNome;
    private ArvoreBMais<ParUsuarioCursoId> indiceUsuario;

    private static final boolean DEBUG = false;

    // abre o arquivo de dados e os índices. Se algo falhar, lança exceção.
    public ArquivoCurso() throws Exception {
        // Padronizado com o nome correto da entidade no plural ("cursos")
        super("cursos", Curso.class.getConstructor());

        try {
            // Caminhos corrigidos para a pasta unificada de dados do projeto
            indiceCodigo = new HashExtensivel<>(
                    ParCodigoId.class.getConstructor(),
                    5,
                    "dados/cursos_codigo.idx",
                    "dados/cursos_codigo.dir");

            indiceNome = new ArvoreBMais<>(
                    ParNomeCursoId.class.getConstructor(),
                    5,
                    "dados/cursos_nome.db");

            indiceUsuario = new ArvoreBMais<>(
                    ParUsuarioCursoId.class.getConstructor(),
                    5,
                    "dados/cursos_usuario.db");

        } catch (Exception e) {
            System.err.println("Erro ao inicializar índices: " + e.getMessage());
            throw e;
        }

        if (DEBUG)
            System.out.println("ArquivoCurso inicializado.");
    }

    // ---------------- CREATE ----------------
    // Cria um novo curso, salva no arquivo e atualiza todos os índices.
    @Override
    public int create(Curso c) throws Exception {

        if (c == null)
            throw new IllegalArgumentException("Curso não pode ser nulo");

        if (c.getNome() == null || c.getNome().isEmpty())
            throw new Exception("Nome do curso é obrigatório");

        if (c.getIdUsuario() <= 0)
            throw new Exception("Curso deve estar vinculado a um usuário válido");

        // Salva no arquivo base (já gera o ID)
        int id = super.create(c);
        c.setID(id);

        try {
            // CORREÇÃO: Construtor do par de código original recebe String e Int
            indiceCodigo.create(new ParCodigoId(c.getCodigo(), id));
            indiceNome.create(new ParNomeCursoId(c.getNome(), id));
            indiceUsuario.create(new ParUsuarioCursoId(c.getIdUsuario(), id));

            if (DEBUG)
                System.out.println("Curso criado com ID: " + id);

        } catch (Exception e) {
            // Se der erro nos índices, desfaz a criação no arquivo base
            super.delete(id);
            throw new Exception("Erro ao criar índices do curso: " + e.getMessage(), e);
        }

        return id;
    }

    // ---------------- READ ----------------
    // Busca curso pelo ID (direto do arquivo).
    @Override
    public Curso read(int id) throws Exception {
        return super.read(id);
    }

    // Busca curso pelo código (hash). Retorna o curso ou null.
    public Curso readCodigo(String codigo) throws Exception {

        if (codigo == null || codigo.trim().isEmpty())
            return null;

        // O HashExtensivel do Kutova busca pelo hash numérico do objeto chave (String
        // ou Par)
        int hashChave = Math.abs(codigo.hashCode());
        ParCodigoId pc = indiceCodigo.read(hashChave);

        if (pc == null)
            return null;

        // CORREÇÃO: Utilizando o método correto do par (getIdCurso ou getId dependendo
        // da classe de par do grupo)
        Curso curso = super.read(pc.getIdCurso());

        // evita colisão de hash testando a string real do NanoID
        if (curso != null && curso.getCodigo().equalsIgnoreCase(codigo.trim())) {
            return curso;
        }

        return null;
    }

    public ArrayList<Curso> readNome(String nome) throws Exception {
        ArrayList<Curso> lista = new ArrayList<>();
        if (nome == null || nome.isEmpty())
            return lista;

        // Cria um objeto de busca: nome preenchido, id = -1 (ignora o id na comparação)
        ParNomeCursoId busca = new ParNomeCursoId(nome, -1);
        ArrayList<ParNomeCursoId> pares = indiceNome.read(busca);

        if (pares != null) {
            for (ParNomeCursoId p : pares) {
                Curso c = super.read(p.getId());
                if (c != null)
                    lista.add(c);
            }
        }
        return lista;
    }

    // Lista todos os cursos de um determinado usuário (útil para o menu "Meus
    // cursos").
    public ArrayList<Curso> listarPorUsuario(int usuarioId) throws Exception {
        ArrayList<Curso> lista = new ArrayList<>();

        // Cria um objeto de busca: usuarioId preenchido, idCurso = -1
        ParUsuarioCursoId busca = new ParUsuarioCursoId(usuarioId, -1);
        ArrayList<ParUsuarioCursoId> pares = indiceUsuario.read(busca);

        if (pares != null) {
            for (ParUsuarioCursoId p : pares) {
                // CORREÇÃO: Mudado de getIdCurso() para getId() que é o padrão visível na
                // classe ParUsuarioCursoId
                Curso c = super.read(p.getId());
                if (c != null)
                    lista.add(c);
            }
        }
        return lista;
    }

    public ArrayList<Curso> listarTodos() throws Exception {
        ArrayList<Curso> lista = new ArrayList<>();

        // Para listar TODOS os elementos de uma Árvore B+,
        // nós passamos um objeto de busca "vazio" ou com valores iniciais mínimos
        // (String vazia e ID -1).
        // O método read() da árvore retornará todos os pares folha ordenados.
        ParNomeCursoId chaveBuscavazia = new ParNomeCursoId("", -1);
        ArrayList<ParNomeCursoId> todosOsPares = indiceNome.read(chaveBuscavazia);

        if (todosOsPares != null) {
            for (ParNomeCursoId par : todosOsPares) {
                // Buscamos apenas os registros cujos IDs estão ativos na árvore B+
                Curso c = super.read(par.getId());
                if (c != null) {
                    lista.add(c);
                }
            }
        }

        return lista;
    }

    // Mantém a ordenação por data exigida pelo menu de inscrições,
    // mas agora alimentado pela listagem limpa vinda da árvore B+
    public ArrayList<Curso> listarCursosOrdenadosPorData() throws Exception {
        ArrayList<Curso> lista = listarTodos();

        // Ordena a lista de cursos baseando-se na data de início
        lista.sort((c1, c2) -> c1.getDataInicio().compareTo(c2.getDataInicio()));

        return lista;
    }

    // ---------------- UPDATE ----------------
    // Atualiza um curso existente. Se nome ou dono mudar, atualiza os índices
    // correspondentes.
    @Override
    public boolean update(Curso novo) throws Exception {

        if (novo == null)
            return false;

        Curso antigo = super.read(novo.getID());
        if (antigo == null)
            return false;

        boolean nomeAlterado = !antigo.getNome().equals(novo.getNome());
        boolean usuarioAlterado = antigo.getIdUsuario() != novo.getIdUsuario();

        try {
            // Remove índices antigos antes de alterar
            if (nomeAlterado)
                indiceNome.delete(new ParNomeCursoId(antigo.getNome(), antigo.getID()));

            if (usuarioAlterado)
                indiceUsuario.delete(new ParUsuarioCursoId(antigo.getIdUsuario(), antigo.getID()));

            // Atualiza no arquivo de dados
            boolean atualizado = super.update(novo);

            if (!atualizado) {
                // Se falhou, recoloca os índices antigos (rollback)
                if (nomeAlterado)
                    indiceNome.create(new ParNomeCursoId(antigo.getNome(), antigo.getID()));

                if (usuarioAlterado)
                    indiceUsuario.create(new ParUsuarioCursoId(antigo.getIdUsuario(), antigo.getID()));

                return false;
            }

            // Insere os novos índices
            if (nomeAlterado)
                indiceNome.create(new ParNomeCursoId(novo.getNome(), novo.getID()));

            if (usuarioAlterado)
                indiceUsuario.create(new ParUsuarioCursoId(novo.getIdUsuario(), novo.getID()));

            return true;

        } catch (Exception e) {
            throw new Exception("Erro ao atualizar curso: " + e.getMessage(), e);
        }
    }

    // ---------------- DELETE ----------------
    // Exclui um curso (logicamente) e remove todas as suas referências nos índices.
    @Override
    public boolean delete(int id) throws Exception {

        Curso c = super.read(id);
        if (c == null)
            return false;

        // CORREÇÃO: Uso de try-finally manual para evitar a exigência do AutoCloseable
        // e tratamento seguro de encerramento do arquivo auxiliar.
        arquivos.ArquivoInscricao arqInscricao = null;
        try {
            arqInscricao = new arquivos.ArquivoInscricao(); // Instanciação explícita com o pacote completo
            ArrayList<entidades.Inscricao> alunosMatriculados = arqInscricao.listarPorCurso(id);
            if (alunosMatriculados != null && !alunosMatriculados.isEmpty()) {
                throw new Exception("Erro: O curso possui alunos inscritos e não pode ser excluído!");
            }
        } finally {
            if (arqInscricao != null) {
                try {
                    arqInscricao.close();
                } catch (Exception e) {
                    // Ignora falhas menores ao fechar a instância temporária
                }
            }
        }

        try {
            indiceCodigo.delete(Math.abs(c.getCodigo().hashCode()));
            indiceNome.delete(new ParNomeCursoId(c.getNome(), id));
            indiceUsuario.delete(new ParUsuarioCursoId(c.getIdUsuario(), id));

        } catch (Exception e) {
            throw new Exception("Erro ao remover índices do curso: " + e.getMessage(), e);
        }

        return super.delete(id);
    }

    // ---------------- CLOSE ----------------
    // Fecha o arquivo de dados e todos os índices.
    @Override
    public void close() throws Exception {
        super.close();

        if (indiceCodigo != null)
            indiceCodigo.close();
        if (indiceNome != null)
            indiceNome.close();
        if (indiceUsuario != null)
            indiceUsuario.close();

        if (DEBUG)
            System.out.println("ArquivoCurso fechado.");
    }
}