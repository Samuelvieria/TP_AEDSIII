package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioCursoId implements InterfaceArvoreBMais<ParUsuarioCursoId> {

    private int usuarioId; // ID do dono do curso
    private int idCurso;   // ID do curso
    private final short TAMANHO = 8; // 4 bytes (int) + 4 bytes (int)

    // Constante para logs de debug
    private static final boolean DEBUG = false;

    // Construtor vazio para o gerenciador de arquivos
    public ParUsuarioCursoId() {
        this(-1, -1);
    }

    // Construtor para busca (apenas a chave primária)
    public ParUsuarioCursoId(int usuarioId) {
        this(usuarioId, -1);
    }

    // Construtor completo
    public ParUsuarioCursoId(int usuarioId, int idCurso) {
        this.usuarioId = usuarioId;
        this.idCurso = idCurso;
    }

    // Adicione ou corrija para estes nomes exatos:
    public int getUsuarioId() {
        return usuarioId;
    }

    public int getId() {   // O ArquivoCurso procura por este nome
        return idCurso;
    }

    @Override
    public ParUsuarioCursoId clone() {
        return new ParUsuarioCursoId(this.usuarioId, this.idCurso);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public int compareTo(ParUsuarioCursoId o) {
        // Primeiro compara pelo ID do usuário (Professor)
        if (this.usuarioId != o.usuarioId) {
            return Integer.compare(this.usuarioId, o.usuarioId);
        }
        
        // Se o ID do curso for -1 (modo busca), considera igual para retornar todos os cursos daquele usuário
        if (this.idCurso == -1 || o.idCurso == -1) {
            return 0;
        }
            
        // Se ambos tiverem IDs, desempata pelo ID do curso (chave secundária)
        return Integer.compare(this.idCurso, o.idCurso);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(usuarioId);
        dos.writeInt(idCurso);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        usuarioId = dis.readInt();
        idCurso = dis.readInt();
    }

    @Override
    public String toString() {
        return String.format("(Dono: %d, Curso: %d)", usuarioId, idCurso);
    }
}