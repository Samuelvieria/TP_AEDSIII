package arquivos;

import entidades.Curso;
import indices.ParCodigoId;
import indices.ParNomeCursoId;
import indices.ParUsuarioCursoId;

import java.util.ArrayList;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.HashExtensivel;

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
        super("curso", Curso.class.getConstructor());

        try {
            indiceCodigo = new HashExtensivel<>(
                    ParCodigoId.class.getConstructor(),
                    4,
                    "./dados/curso/indiceCodigo.d.db",
                    "./dados/curso/indiceCodigo.c.db");

            indiceNome = new ArvoreBMais<>(
                    ParNomeCursoId.class.getConstructor(),
                    5,
                    "./dados/curso/indiceNome.db");

            indiceUsuario = new ArvoreBMais<>(
                    ParUsuarioCursoId.class.getConstructor(),
                    5,
                    "./dados/curso/indiceUsuario.db");

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

        try {
            // Insere nos três índices
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
        int hash = Math.abs(codigo.hashCode());
        ParCodigoId pc = indiceCodigo.read(hash);
        if (pc == null)
            return null;
        return super.read(pc.getIdCurso());
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
                Curso c = super.read(p.getId());
                if (c != null)
                    lista.add(c);
            }
        }
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

        try {
            indiceCodigo.delete(id);
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