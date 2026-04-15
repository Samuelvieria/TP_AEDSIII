package indices;

import aed3.RegistroArvoreBMais;
import java.io.*;

public class ParUsuarioCursoId implements RegistroArvoreBMais<ParUsuarioCursoId> {

    private int usuarioId;
    private int idCurso;

    public ParUsuarioCursoId() {
        this(-1, -1);
    }

    public ParUsuarioCursoId(int usuarioId, int idCurso) {
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
    public int compareTo(ParUsuarioCursoId o) {
        int comp = Integer.compare(this.usuarioId, o.usuarioId);
        if (comp != 0) return comp;
        return Integer.compare(this.idCurso, o.idCurso);
    }

    @Override
    public short size() {
        return 8;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(ba);

        dos.writeInt(usuarioId);
        dos.writeInt(idCurso);

        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);

        usuarioId = di.readInt();
        idCurso = di.readInt();
    }
}