package entidades;

import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Usuario implements InterfaceEntidade {
    
    private int id;
    private String nome;
    private String email;
    private int hashSenha;
    private String perguntaSecreta;
    private int hashResposta;
    
    // Construtores
    public Usuario() {
        this(-1, "", "", -1, "", -1);
    }
    
    public Usuario(String nome, String email, String senha, String pergunta, String resposta) {
        this(-1, nome, email, senha.hashCode(), pergunta, resposta.hashCode());
    }
    
    public Usuario(int id, String nome, String email, int hashSenha, String pergunta, int hashResposta) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.perguntaSecreta = pergunta;
        this.hashResposta = hashResposta;
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
    
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.nome = nome; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email; 
    }
    
    public int getHashSenha() { 
        return hashSenha; 
    }
    
    public void setHashSenha(int hash) { 
        this.hashSenha = hash; 
    }
    
    public void setSenha(String senha) { 
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres");
        }
        this.hashSenha = senha.hashCode(); 
    }
    
    public boolean verificaSenha(String senha) { 
        return this.hashSenha == senha.hashCode(); 
    }
    
    public String getPerguntaSecreta() { 
        return perguntaSecreta; 
    }
    
    public void setPerguntaSecreta(String pergunta) {
        if (pergunta == null || pergunta.trim().isEmpty()) {
            throw new IllegalArgumentException("Pergunta secreta não pode ser vazia");
        }
        this.perguntaSecreta = pergunta; 
    }
    
    public int getHashResposta() { 
        return hashResposta; 
    }
    
    public void setHashResposta(int hash) { 
        this.hashResposta = hash; 
    }
    
    public void setResposta(String resposta) {
        if (resposta == null || resposta.trim().isEmpty()) {
            throw new IllegalArgumentException("Resposta secreta não pode ser vazia");
        }
        this.hashResposta = resposta.hashCode(); 
    }
    
    public boolean verificaResposta(String resposta) { 
        return this.hashResposta == resposta.hashCode(); 
    }
    
    @Override
    public String toString() {
        return "ID........: " + id +
               "\nNome......: " + nome +
               "\nEmail.....: " + email +
               "\nPergunta..: " + perguntaSecreta;
    }
    
    // Serialização
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(email);
        dos.writeInt(hashSenha);
        dos.writeUTF(perguntaSecreta);
        dos.writeInt(hashResposta);
        //System.out.println("Serializando usuário: " + this.toString());
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        nome = dis.readUTF();
        email = dis.readUTF();
        hashSenha = dis.readInt();
        perguntaSecreta = dis.readUTF();
        hashResposta = dis.readInt();
        //System.out.println("Desserializando usuário: " + this.toString());
    }
}