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

    private HashExtensivel<ParCodigoId> indiceCodigo;
    private ArvoreBMais<ParNomeCursoId> indiceNome;
    private ArvoreBMais<ParUsuarioCursoId> indiceUsuario;

    private static final boolean DEBUG = false;

    public ArquivoCurso() throws Exception {
        super("curso", Curso.class.getConstructor());

        try {
            indiceCodigo = new HashExtensivel<>(
                    ParCodigoId.class.getConstructor(),
                    4,
                    "./dados/curso/indiceCodigo.d.db",
                    "./dados/curso/indiceCodigo.c.db"
            );

            indiceNome = new ArvoreBMais<>(
                    ParNomeCursoId.class.getConstructor(),
                    5,
                    "./dados/curso/indiceNome.db"
            );

            indiceUsuario = new ArvoreBMais<>(
                    ParUsuarioCursoId.class.getConstructor(),
                    5,
                    "./dados/curso/indiceUsuario.db"
            );

        } catch (Exception e) {
            System.err.println("Erro ao inicializar índices: " + e.getMessage());
            throw e;
        }

        if (DEBUG) System.out.println("ArquivoCurso inicializado.");
    }

    // ---------------- CREATE ----------------
    @Override
    public int create(Curso c) throws Exception {

        if (c == null)
            throw new IllegalArgumentException("Curso não pode ser nulo");

        if (c.getNome() == null || c.getNome().isEmpty())
            throw new Exception("Nome do curso é obrigatório");

        if (c.getUsuarioId() <= 0)
            throw new Exception("Curso deve estar vinculado a um usuário válido");

        int id = super.create(c);

        try {
            indiceCodigo.create(new ParCodigoId(id, c.getEndereco()));
            indiceNome.create(new ParNomeCursoId(c.getNome(), id));
            indiceUsuario.create(new ParUsuarioCursoId(c.getUsuarioId(), id));

            if (DEBUG) System.out.println("Curso criado com ID: " + id);

        } catch (Exception e) {
            super.delete(id);
            throw new Exception("Erro ao criar índices do curso: " + e.getMessage(), e);
        }

        return id;
    }

    // ---------------- READ ----------------
    @Override
    public Curso read(int id) throws Exception {
        return super.read(id);
    }

    public Curso readCodigo(int id) throws Exception {
        ParCodigoId pc = indiceCodigo.read(id);
        if (pc == null) return null;
        return super.read(pc.getEndereco());
    }

    public ArrayList<Curso> readNome(String nome) throws Exception {
        ArrayList<Curso> lista = new ArrayList<>();

        if (nome == null || nome.isEmpty())
            return lista;

        ArrayList<ParNomeCursoId> pares = indiceNome.read(nome);

        if (pares != null) {
            for (ParNomeCursoId p : pares) {
                Curso c = super.read(p.getId());
                if (c != null) lista.add(c);
            }
        }

        return lista;
    }

    public ArrayList<Curso> listarPorUsuario(int usuarioId) throws Exception {
        ArrayList<Curso> lista = new ArrayList<>();

        ArrayList<ParUsuarioCursoId> pares = indiceUsuario.read(usuarioId);

        if (pares != null) {
            for (ParUsuarioCursoId p : pares) {
                Curso c = super.read(p.getId());
                if (c != null) lista.add(c);
            }
        }

        return lista;
    }

    // ---------------- UPDATE ----------------
    @Override
    public boolean update(Curso novo) throws Exception {

        if (novo == null) return false;

        Curso antigo = super.read(novo.getID());
        if (antigo == null) return false;

        boolean nomeAlterado = !antigo.getNome().equals(novo.getNome());
        boolean usuarioAlterado = antigo.getUsuarioId() != novo.getUsuarioId();

        try {
            // remover índices antigos
            if (nomeAlterado)
                indiceNome.delete(new ParNomeCursoId(antigo.getNome(), antigo.getID()));

            if (usuarioAlterado)
                indiceUsuario.delete(new ParUsuarioCursoId(antigo.getUsuarioId(), antigo.getID()));

            boolean atualizado = super.update(novo);

            if (!atualizado) {
                // rollback
                if (nomeAlterado)
                    indiceNome.create(new ParNomeCursoId(antigo.getNome(), antigo.getID()));

                if (usuarioAlterado)
                    indiceUsuario.create(new ParUsuarioCursoId(antigo.getUsuarioId(), antigo.getID()));

                return false;
            }

            // recriar índices novos
            if (nomeAlterado)
                indiceNome.create(new ParNomeCursoId(novo.getNome(), novo.getID()));

            if (usuarioAlterado)
                indiceUsuario.create(new ParUsuarioCursoId(novo.getUsuarioId(), novo.getID()));

            return true;

        } catch (Exception e) {
            throw new Exception("Erro ao atualizar curso: " + e.getMessage(), e);
        }
    }

    // ---------------- DELETE ----------------
    @Override
    public boolean delete(int id) throws Exception {

        Curso c = super.read(id);
        if (c == null) return false;

        try {
            indiceCodigo.delete(id);
            indiceNome.delete(new ParNomeCursoId(c.getNome(), id));
            indiceUsuario.delete(new ParUsuarioCursoId(c.getUsuarioId(), id));

        } catch (Exception e) {
            throw new Exception("Erro ao remover índices do curso: " + e.getMessage(), e);
        }

        return super.delete(id);
    }

    // ---------------- CLOSE ----------------
    @Override
    public void close() throws Exception {
        super.close();

        if (indiceCodigo != null) indiceCodigo.close();
        if (indiceNome != null) indiceNome.close();
        if (indiceUsuario != null) indiceUsuario.close();

        if (DEBUG) System.out.println("ArquivoCurso fechado.");
    }
}