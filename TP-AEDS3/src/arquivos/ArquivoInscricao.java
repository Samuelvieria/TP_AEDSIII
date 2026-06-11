package arquivos;
import entidades.Curso;
import arquivos.ArquivoCurso;
import entidades.Curso;
import entidades.Inscricao;
import indices.ParUsuarioInscricao;
import indices.ParCursoInscricao;

import java.util.ArrayList;
import aed3.Arquivo;
import aed3.ArvoreBMais;

public class ArquivoInscricao extends Arquivo<Inscricao> {

    // Índices conforme o padrão do professor
    private ArvoreBMais<ParUsuarioInscricao> indiceUsuario;
    private ArvoreBMais<ParCursoInscricao> indiceCurso;

    public ArquivoInscricao() throws Exception {
        super("inscricoes", Inscricao.class.getConstructor());

        // O caminho dos dados deve seguir o padrão da pasta 'dados' do projeto
        indiceUsuario = new ArvoreBMais<>(
                ParUsuarioInscricao.class.getConstructor(),
                5,
                "dados/inscricoes_usuario.db");
        indiceCurso = new ArvoreBMais<>(
                ParCursoInscricao.class.getConstructor(),
                5,
                "dados/inscricoes_curso.db");
    }

    @Override
    public int create(Inscricao obj) throws Exception {
        if (obj == null)
            throw new IllegalArgumentException("Inscrição inválida.");

        // VERIFICAR SE O CURSO EXISTE E ESTÁ ATIVO
        Curso curso = new ArquivoCurso().read(obj.getIdCurso());
        if (curso == null) {
            throw new Exception("Erro: Curso não encontrado!");
        }
        if (curso.getEstado() != 0) {
            String msgEstado;
            switch (curso.getEstado()) {
                case 1:
                    msgEstado = "INSCRIÇÕES ENCERRADAS";
                    break;
                case 2:
                    msgEstado = "CURSO CONCLUÍDO";
                    break;
                case 3:
                    msgEstado = "CURSO CANCELADO";
                    break;
                default:
                    msgEstado = "ESTADO INVÁLIDO";
                    break;
            }
            throw new Exception("Erro: Não é possível se inscrever. O curso está com status: " + msgEstado);
        }

        // CORREÇÃO: Busca usando o par completo (idUsuario, -1) para mapeamento seguro
        ArrayList<ParUsuarioInscricao> inscricoesExistentes = indiceUsuario
                .read(new ParUsuarioInscricao(obj.getIdUsuario(), -1));

        if (inscricoesExistentes != null) {
            for (ParUsuarioInscricao p : inscricoesExistentes) {
                Inscricao aux = super.read(p.getIdInscricao());
                if (aux != null && aux.getIdCurso() == obj.getIdCurso()) {
                    throw new Exception("Erro: O usuário já está inscrito neste curso!");
                }
            }
        }

        // Se passou pela verificação, segue o fluxo normal de persistência física
        int id = super.create(obj);
        obj.setID(id);

        try {
            indiceUsuario.create(new ParUsuarioInscricao(obj.getIdUsuario(), id));
            indiceCurso.create(new ParCursoInscricao(obj.getIdCurso(), id));
        } catch (Exception e) {
            super.delete(id); // Rollback físico se falhar o índice
            throw new Exception("Erro ao criar índices de inscrição: " + e.getMessage());
        }

        return id;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Inscricao obj = super.read(id);

        if (obj != null) {
            // Remove dos índices usando os dados que acabamos de ler
            indiceUsuario.delete(new ParUsuarioInscricao(obj.getIdUsuario(), id));
            indiceCurso.delete(new ParCursoInscricao(obj.getIdCurso(), id));

            // Deleta o registro físico
            return super.delete(id);
        }
        return false;
    }

    @Override
    public boolean update(Inscricao nova) throws Exception {
        if (nova == null)
            return false;

        // 1. Lê o estado antigo do registro para saber o que estava nos índices
        Inscricao antiga = super.read(nova.getID());
        if (antiga == null)
            return false; // Registro não encontrado para atualizar

        // Verifica se houve mudança nas chaves indexadas
        boolean usuarioAlterado = antiga.getIdUsuario() != nova.getIdUsuario();
        boolean cursoAlterado = antiga.getIdCurso() != nova.getIdCurso();

        try {
            // 2. Remove os índices antigos se as chaves mudaram
            if (usuarioAlterado)
                indiceUsuario.delete(new ParUsuarioInscricao(antiga.getIdUsuario(), antiga.getID()));
            if (cursoAlterado)
                indiceCurso.delete(new ParCursoInscricao(antiga.getIdCurso(), antiga.getID()));

            // 3. Atualiza o registro no arquivo físico base
            boolean atualizado = super.update(nova);

            if (!atualizado) {
                // Rollback: se a escrita no arquivo falhar, reinsere os índices antigos
                if (usuarioAlterado)
                    indiceUsuario.create(new ParUsuarioInscricao(antiga.getIdUsuario(), antiga.getID()));
                if (cursoAlterado)
                    indiceCurso.create(new ParCursoInscricao(antiga.getIdCurso(), antiga.getID()));
                return false;
            }

            // 4. Insere os novos índices atualizados
            if (usuarioAlterado)
                indiceUsuario.create(new ParUsuarioInscricao(nova.getIdUsuario(), nova.getID()));
            if (cursoAlterado)
                indiceCurso.create(new ParCursoInscricao(nova.getIdCurso(), nova.getID()));

            return true;

        } catch (Exception e) {
            // Rollback completo em caso de exceção
            if (usuarioAlterado)
                indiceUsuario.create(new ParUsuarioInscricao(antiga.getIdUsuario(), antiga.getID()));
            if (cursoAlterado)
                indiceCurso.create(new ParCursoInscricao(antiga.getIdCurso(), antiga.getID()));
            throw new Exception("Erro ao atualizar índices da inscrição: " + e.getMessage(), e);
        }
    }

    // --- MÉTODOS DE BUSCA DA ARVORE B+ ---

    public ArrayList<Inscricao> listarPorUsuario(int idUsuario) throws Exception {
        ArrayList<Inscricao> lista = new ArrayList<>();

        // CORREÇÃO: Passando o construtor com o id de registro -1 para listagem por
        // prefixo de chave
        ArrayList<ParUsuarioInscricao> pui = indiceUsuario.read(new ParUsuarioInscricao(idUsuario, -1));

        if (pui != null) {
            for (int i = 0; i < pui.size(); i++) {
                Inscricao ins = super.read(pui.get(i).getIdInscricao());
                if (ins != null) {
                    lista.add(ins);
                }
            }
        }
        return lista;
    }

    public ArrayList<Inscricao> listarPorCurso(int idCurso) throws Exception {
        ArrayList<Inscricao> lista = new ArrayList<>();

        // CORREÇÃO: Passando o construtor com o id de registro -1 para buscar relações
        // do curso
        ArrayList<ParCursoInscricao> pci = indiceCurso.read(new ParCursoInscricao(idCurso, -1));

        if (pci != null) {
            for (int i = 0; i < pci.size(); i++) {
                Inscricao ins = super.read(pci.get(i).getIdInscricao());
                if (ins != null) {
                    lista.add(ins);
                }
            }
        }
        return lista;
    }

    public Inscricao buscarRelacao(int idUsuario, int idCurso) throws Exception {
        ArrayList<Inscricao> inscricoesDoUsuario = this.listarPorUsuario(idUsuario);

        if (inscricoesDoUsuario != null) {
            for (Inscricao insc : inscricoesDoUsuario) {
                if (insc.getIdCurso() == idCurso) {
                    return insc;
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (indiceUsuario != null)
            indiceUsuario.close();
        if (indiceCurso != null)
            indiceCurso.close();
    }
}