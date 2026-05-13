package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioInscricao implements InterfaceArvoreBMais<ParUsuarioInscricao> {

    private int idUsuario;
    private int idInscricao;
    private final short TAMANHO = 8; // 4 bytes (idUsuario) + 4 bytes (idInscricao)

    private static final boolean DEBUG = false;

    public ParUsuarioInscricao() {
        this(-1, -1);
    }

    public ParUsuarioInscricao(int idUsuario) {
        this(idUsuario, -1);
    }

    public ParUsuarioInscricao(int idUsuario, int idInscricao) {
        this.idUsuario = idUsuario;
        this.idInscricao = idInscricao;
    }

    public int getIdInscricao() {
    return this.idInscricao;
}

    @Override
    public ParUsuarioInscricao clone() {
        return new ParUsuarioInscricao(this.idUsuario, this.idInscricao);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public int compareTo(ParUsuarioInscricao o) {
        // Primeiro compara pelo ID do usuário (chave principal)
        int comp = Integer.compare(this.idUsuario, o.idUsuario);
        if (comp != 0) return comp;

        // Se o ID da inscrição for -1 (modo busca), considera igual se o usuário for o mesmo
        if (this.idInscricao == -1 || o.idInscricao == -1) return 0;

        // Caso contrário, desempata pelo ID da inscrição (chave secundária)
        return Integer.compare(this.idInscricao, o.idInscricao);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idUsuario);
        dos.writeInt(idInscricao);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        idUsuario = dis.readInt();
        idInscricao = dis.readInt();
    }

    @Override
    public String toString() {
        return String.format("(Usuário: %d, Inscrição: %d)", idUsuario, idInscricao);
    }
}