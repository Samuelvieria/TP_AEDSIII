package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParCursoInscricao implements InterfaceArvoreBMais<ParCursoInscricao> {

    private int idCurso;
    private int idInscricao;
    private final short TAMANHO = 8; // 4 bytes (idCurso) + 4 bytes (idInscricao)

    public ParCursoInscricao() {
        this(-1, -1);
    }

    public ParCursoInscricao(int idCurso) {
        this(idCurso, -1);
    }

    public ParCursoInscricao(int idCurso, int idInscricao) {
        this.idCurso = idCurso;
        this.idInscricao = idInscricao;
    }

    public int getIdInscricao() {
    return this.idInscricao;
}

    @Override
    public ParCursoInscricao clone() {
        return new ParCursoInscricao(this.idCurso, this.idInscricao);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public int compareTo(ParCursoInscricao o) {
        // Primeiro compara pelo ID do curso (chave principal)
        int comp = Integer.compare(this.idCurso, o.idCurso);
        if (comp != 0) return comp;

        // Se o ID da inscrição for -1 (modo busca), considera igual se o curso for o mesmo
        if (this.idInscricao == -1 || o.idInscricao == -1) return 0;

        // Caso contrário, desempata pelo ID da inscrição (chave secundária)
        return Integer.compare(this.idInscricao, o.idInscricao);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idCurso);
        dos.writeInt(idInscricao);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idCurso = dis.readInt();
        idInscricao = dis.readInt();
    }

    @Override
    public String toString() {
        return String.format("(Curso: %d, Inscrição: %d)", idCurso, idInscricao);
    }
}