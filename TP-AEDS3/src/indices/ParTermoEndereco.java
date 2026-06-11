package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Classe que representa um par (termo, endereço) para indexação em árvore B+.
 * Implementa InterfaceArvoreBMais para ser armazenada em estruturas de árvore do projeto.
 */
public class ParTermoEndereco implements InterfaceArvoreBMais<ParTermoEndereco> {
    
    private String termo;      // Termo indexado (chave)
    private long endereco;     // Endereço onde estão armazenados os pares (idCurso, TF)

    public ParTermoEndereco() {
        this.termo = "";
        this.endereco = -1;
    }

    public ParTermoEndereco(String termo, long endereco) {
        this.termo = termo;
        this.endereco = endereco;
    }

    // Getters
    public String getTermo() {
        return termo;
    }

    public long getEndereco() {
        return endereco;
    }

    // Setters
    public void setEndereco(long novoEndereco) {
        this.endereco = novoEndereco;
    }

    @Override
    public short size() {
        return 108;  // 100 bytes para termo + 8 bytes para long
    }

    @Override
    public int compareTo(ParTermoEndereco outro) {
        if (outro == null)
            return 1;
        return this.termo.compareTo(outro.termo);
    }

    @Override
    public ParTermoEndereco clone() {
        return new ParTermoEndereco(this.termo, this.endereco);
    }

    @Override
    public String toString() {
        return "(" + this.termo + ";" + this.endereco + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // Escrever o termo com tamanho fixo de 100 caracteres
        byte[] termoBytes = new byte[100];
        byte[] temp = this.termo.getBytes("UTF-8");
        System.arraycopy(temp, 0, termoBytes, 0, Math.min(temp.length, 100));
        dos.write(termoBytes);
        
        // Escrever o endereço
        dos.writeLong(this.endereco);
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        // Ler o termo
        byte[] termoBytes = new byte[100];
        dis.readFully(termoBytes);
        this.termo = new String(termoBytes, "UTF-8").trim();
        
        // Ler o endereço
        this.endereco = dis.readLong();
    }
}
