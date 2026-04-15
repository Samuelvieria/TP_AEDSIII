package entidades;

import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

public class Curso implements InterfaceEntidade {

    private int id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private String codigo;       // 10 caracteres
    private byte estado;         // 0=ativo, 1=encerrado, 2=concluído, 3=cancelado
    private int idUsuario;       // dono do curso

    // Construtor vazio
    public Curso() {
        this(-1, "", "", LocalDate.now(), "", (byte)0, -1);
    }

    // Construtor sem ID (para criação antes de gerar ID)
    public Curso(String nome, String descricao, LocalDate dataInicio, String codigo, byte estado, int idUsuario) {
        this(-1, nome, descricao, dataInicio, codigo, estado, idUsuario);
    }

    // Construtor completo
    public Curso(int id, String nome, String descricao, LocalDate dataInicio, String codigo, byte estado, int idUsuario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.codigo = codigo;
        this.estado = estado;
        this.idUsuario = idUsuario;
    }

    // GETTERS E SETTERS
    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public byte getEstado() {
        return estado;
    }

    public void setEstado(byte estado) {
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Serialização compatível com Arquivo<T>
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(descricao);
        dos.writeInt((int) dataInicio.toEpochDay()); // salva data como int (dias desde 1970-01-01)
        dos.writeUTF(codigo);
        dos.writeByte(estado);
        dos.writeInt(idUsuario);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        nome = dis.readUTF();
        descricao = dis.readUTF();
        dataInicio = LocalDate.ofEpochDay(dis.readInt());
        codigo = dis.readUTF();
        estado = dis.readByte();
        idUsuario = dis.readInt();
    }

    @Override
    public String toString() {
        return "ID........: " + id +
               "\nNome......: " + nome +
               "\nDescrição.: " + descricao +
               "\nData início: " + dataInicio +
               "\nCódigo....: " + codigo +
               "\nEstado....: " + estado +
               "\nID Usuário: " + idUsuario;
    }
}