package entidades;

import aed3.InterfaceArvoreBMais;
import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class CursoUsuario implements InterfaceEntidade, InterfaceArvoreBMais<CursoUsuario> {

    private int idCursoUsuario;
    private int idCurso;
    private int idUsuario;
    private LocalDate dataInscricao;

    // Construtor vazio
    public CursoUsuario() {
        this(-1, -1, -1, LocalDate.now());
    }

    // Construtor completo
    public CursoUsuario(int idCursoUsuario, int idCurso, int idUsuario, LocalDate dataInscricao) {
        this.idCursoUsuario = idCursoUsuario;
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
        this.dataInscricao = dataInscricao;
    }

    // ---------------- MÉTODOS EXIGIDOS PELA INTERFACE ARVORE B+ ----------------

    @Override
    public CursoUsuario clone() {
        return new CursoUsuario(this.idCursoUsuario, this.idCurso, this.idUsuario, this.dataInscricao);
    }

    @Override
    public int compareTo(CursoUsuario outro) {
        // A árvore B+ organiza e busca os registros baseando-se no ID do Curso
        return Integer.compare(this.idCurso, outro.idCurso);
    }

    @Override
    public short size() {
        // Retorna o tamanho fixo em bytes da estrutura serializada (4 ints * 4 bytes =
        // 16)
        return 16;
    }

    // ---------------- INTERFACE ENTIDADE (CONTRATO DO MOTOR BASE) ----------------
    @Override
    public int getID() {
        return idCursoUsuario;
    }

    @Override
    public void setID(int id) {
        this.idCursoUsuario = id;
    }

    // ---------------- GETTERS E SETTERS TRADICIONAIS ----------------
    public int getIdCursoUsuario() {
        return idCursoUsuario;
    }

    public void setIdCursoUsuario(int idCursoUsuario) {
        this.idCursoUsuario = idCursoUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    // ---------------- SERIALIZAÇÃO BINÁRIA (AJUSTADA COM IOEXCEPTION)
    // ----------------
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idCursoUsuario);
        dos.writeInt(this.idCurso);
        dos.writeInt(this.idUsuario);
        dos.writeInt((int) this.dataInscricao.toEpochDay());

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idCursoUsuario = dis.readInt();
        this.idCurso = dis.readInt();
        this.idUsuario = dis.readInt();
        this.dataInscricao = LocalDate.ofEpochDay(dis.readInt());
    }

    @Override
    public String toString() {
        return "ID Inscrição: " + idCursoUsuario +
                "\nID do Curso: " + idCurso +
                "\nID do Usuário: " + idUsuario +
                "\nData de Inscrição: " + dataInscricao;
    }
}