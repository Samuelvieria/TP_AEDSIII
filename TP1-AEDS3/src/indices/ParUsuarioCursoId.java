package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioCursoId implements InterfaceArvoreBMais<ParUsuarioCursoId> {

    private int usuarioId;
    private int idCurso;
    private final short TAMANHO = 8;  // 4 bytes (usuarioId) + 4 bytes (idCurso)

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public ParUsuarioCursoId() throws Exception {
        this(-1, -1);
    }

    public ParUsuarioCursoId(int usuarioId) throws Exception {
        this(usuarioId, -1);
    }

    public ParUsuarioCursoId(int usuarioId, int idCurso) throws Exception {
        this.usuarioId = usuarioId;
        this.idCurso = idCurso;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public int getId() {
        return idCurso;
    }

    @Override
    public ParUsuarioCursoId clone() {
        try {
            return new ParUsuarioCursoId(this.usuarioId, this.idCurso);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public int compareTo(ParUsuarioCursoId o) {
        // Primeiro compara pelo ID do usuário
        int comp = Integer.compare(this.usuarioId, o.usuarioId);
        if (comp != 0) return comp;

        // Se o ID do curso for -1 (modo busca), considera igual se usuário for igual
        if (this.idCurso == -1 || o.idCurso == -1) return 0;

        // Caso contrário, desempata pelo ID do curso
        return Integer.compare(this.idCurso, o.idCurso);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(usuarioId);
        dos.writeInt(idCurso);

        if (DEBUG) System.out.println("ParUsuarioCursoId serializado: " + this.toString());
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        usuarioId = dis.readInt();
        idCurso = dis.readInt();

        if (DEBUG) System.out.println("ParUsuarioCursoId desserializado: " + this.toString());
    }

    @Override
    public String toString() {
        return "(" + usuarioId + ";" + idCurso + ")";
    }
}
