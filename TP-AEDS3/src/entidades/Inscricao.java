package entidades;

import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Inscricao implements InterfaceEntidade {
    
    private int id;
    private int idUsuario;
    private int idCurso;
    private long dataInscricao; // Armazena o timestamp da inscrição

    // Construtor vazio para o Arquivo.java instanciar via reflexão
    public Inscricao() {
        this.id = -1;
        this.idUsuario = -1;
        this.idCurso = -1;
        this.dataInscricao = -1;
    }

    // Construtor para novas inscrições (o ID será gerado pelo Arquivo.java)
    public Inscricao(int idUsuario, int idCurso) {
        this.id = -1;
        this.idUsuario = idUsuario;
        this.idCurso = idCurso;
        this.dataInscricao = System.currentTimeMillis();
    }

    // Getters e Setters
    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public long getDataInscricao() {
        return dataInscricao;
    }

    // SERIALIZAÇÃO
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeInt(idCurso);
        dos.writeLong(dataInscricao);
        return baos.toByteArray();
    }

    // DESSERIALIZAÇÃO
    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idUsuario = dis.readInt();
        idCurso = dis.readInt();
        dataInscricao = dis.readLong();
    }

    @Override
    public String toString() {
        return "ID Inscrição: " + id +
               " | ID Usuário: " + idUsuario +
               " | ID Curso: " + idCurso +
               " | Data: " + new java.util.Date(dataInscricao);
    }
}