package indices; 

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ParEmailId implements InterfaceHashExtensivel {
    
    private String email;       // chave
    private int id;             // valor (ID do usuário)
    private final short TAMANHO = 64;  // 60 bytes para email + 4 para int
    
    public ParEmailId() {
        this.email = "";
        this.id = -1;
    }
    
    public ParEmailId(String email, int id) throws Exception {
        if (email == null || email.isEmpty()) {
            throw new Exception("Email não pode ser vazio");
        }
        if (email.length() > 60) {
            throw new Exception("Email muito longo (máx. 60 caracteres)");
        }
        this.email = email;
        this.id = id;
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public int getId() { 
        return id; 
    }
    
    @Override
    public int hashCode() {
        return Math.abs(email.hashCode());
    }
    
    @Override
    public short size() {
        return TAMANHO;
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Converte email para bytes e fixa tamanho 60
        byte[] emailBytes = email.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[60];
        System.arraycopy(emailBytes, 0, buffer, 0, Math.min(emailBytes.length, 60));
        dos.write(buffer);
        dos.writeInt(id);
        
        //System.out.println("ParEmailId serializado: " + this.toString());
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        byte[] buffer = new byte[60];
        dis.read(buffer);
        // Remove zeros à direita para obter string real
        int len = 0;
        while (len < 60 && buffer[len] != 0) len++;
        email = new String(buffer, 0, len, StandardCharsets.UTF_8);
        
        id = dis.readInt();
        //System.out.println("ParEmailId desserializado: " + this.toString());
    }
    
    @Override
    public String toString() {
        return "(" + email + ";" + id + ")";
    }
}