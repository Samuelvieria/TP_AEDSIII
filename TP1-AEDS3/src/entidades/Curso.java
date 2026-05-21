package entidades;

import aed3.InterfaceEntidade;
import arquivos.ArquivoInscricao;
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
    private String codigo; // 10 caracteres (NanoID)
    private byte estado; // 0=ativo, 1=encerrado, 2=concluído, 3=cancelado
    private int idUsuario; // dono do curso

    // Construtor vazio
    public Curso() {
        this(-1, "", "", LocalDate.now(), "", (byte) 0, -1);
    }

    // Construtor sem ID (para criação antes de gerar ID automático)
    public Curso(String nome, String descricao, LocalDate dataInicio, String codigo, byte estado, int idUsuario) {
        this(-1, nome, descricao, dataInicio, codigo, estado, idUsuario);
    }

    // Construtor completo
    public Curso(int id, String nome, String descricao, LocalDate dataInicio, String codigo, byte estado,
            int idUsuario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.codigo = codigo;
        this.estado = estado;
        this.idUsuario = idUsuario;
    }

    // ---------------- GETTERS E SETTERS ----------------
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

    // Mantido por compatibilidade com códigos alternativos do grupo
    public int getUsuarioId() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // ---------------- REGRAS DE NEGÓCIO DE ESTADO (TP_02) ----------------
    public boolean estaAtivo() {
        return estado == 0;
    }

    public boolean inscricoesEncerradas() {
        return estado == 1;
    }

    public boolean estaConcluido() {
        return estado == 2;
    }

    public boolean estaCancelado() {
        return estado == 3;
    }

    public boolean aceitaInscricao() {
        return estado == 0;
    }

    // Verifica se o curso possui alunos matriculados usando sua Árvore B+ de
    // inscrições
    public boolean possuiInscricoes(int idCurso) {
        ArquivoInscricao arqInscricao = null;
        try {
            arqInscricao = new ArquivoInscricao();

            // CORREÇÃO: Busca usando o método do seu controle/índice para validar se a
            // lista de alunos não está vazia
            return !arqInscricao.listarPorCurso(idCurso).isEmpty();

        } catch (Exception e) {
            System.err.println("Erro ao verificar inscrições do curso: " + e.getMessage());
            // Por segurança em falhas de leitura, retorna true para blindar o arquivo
            // contra deleções órfãs
            return true;
        } finally {
            if (arqInscricao != null) {
                try {
                    arqInscricao.close();
                } catch (Exception e) {
                    // Fechamento silencioso do recurso local
                }
            }
        }
    }

    // ---------------- SERIALIZAÇÃO (COMPATÍVEL COM ARQUIVO BINÁRIO GENÉRICO)
    // ----------------
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(descricao);
        dos.writeInt((int) dataInicio.toEpochDay()); // Salva data como int (dias desde 1970-01-01)
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