package arquivos;

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

        // Validação de duplicatas
        // Antes de criar, verificamos se esse usuário já está nesse curso
        ArrayList<ParUsuarioInscricao> inscricoesExistentes = indiceUsuario
                .read(new ParUsuarioInscricao(obj.getIdUsuario()));

        for (ParUsuarioInscricao p : inscricoesExistentes) {
            // Lemos a inscrição completa para checar o ID do curso
            Inscricao aux = super.read(p.getIdInscricao());
            if (aux != null && aux.getIdCurso() == obj.getIdCurso()) {
                // Se encontrar uma inscrição com o mesmo par Usuário/Curso, cancela a inserção
                throw new Exception("Erro: O usuário já está inscrito neste curso!");
            }
        }

        // Se passou pela verificação, segue o fluxo normal
        int id = super.create(obj);
        obj.setID(id);

        indiceUsuario.create(new ParUsuarioInscricao(obj.getIdUsuario(), id));
        indiceCurso.create(new ParCursoInscricao(obj.getIdCurso(), id));

        return id;
    }

    @Override
    public boolean delete(int id) throws Exception {
        // 1. Lê o objeto para saber as chaves (usuário e curso) que estão nos índices
        Inscricao obj = super.read(id);

        if (obj != null) {
            // 2. Remove dos índices usando os dados que acabamos de ler
            // IMPORTANTE: O segundo parâmetro do par deve ser o ID do registro (o mesmo
            // usado no create)
            boolean d1 = indiceUsuario.delete(new ParUsuarioInscricao(obj.getIdUsuario(), id));
            boolean d2 = indiceCurso.delete(new ParCursoInscricao(obj.getIdCurso(), id));

            // 3. Deleta o registro físico
            return super.delete(id);
        }
        return false;
    }

    // --- MÉTODOS DE BUSCA DA ARVORE B+ ---

    public ArrayList<Inscricao> listarPorUsuario(int idUsuario) throws Exception {
        ArrayList<Inscricao> lista = new ArrayList<>();

        // O método read da ArvoreBMais do professor retorna um ArrayList<T>
        ArrayList<ParUsuarioInscricao> pui = indiceUsuario.read(new ParUsuarioInscricao(idUsuario));

        for (int i = 0; i < pui.size(); i++) {
            // Buscamos o objeto completo no arquivo de dados usando o ID guardado no par
            Inscricao ins = super.read(pui.get(i).getIdInscricao());
            if (ins != null) {
                lista.add(ins);
            }
        }
        return lista;
    }

    public ArrayList<Inscricao> listarPorCurso(int idCurso) throws Exception {
        ArrayList<Inscricao> lista = new ArrayList<>();

        ArrayList<ParCursoInscricao> pci = indiceCurso.read(new ParCursoInscricao(idCurso));

        for (int i = 0; i < pci.size(); i++) {
            Inscricao ins = super.read(pci.get(i).getIdInscricao());
            if (ins != null) {
                lista.add(ins);
            }
        }
        return lista;
    }

    @Override
    public void close() throws Exception {
        super.close();
        indiceUsuario.close();
        indiceCurso.close();
    }
}