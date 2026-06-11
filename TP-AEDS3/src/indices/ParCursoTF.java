package indices;

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Classe que representa um par (idCurso, TF) para armazenamento no índice invertido.
 * Implementa InterfaceHashExtensivel para ser armazenada em estruturas de dados do projeto.
 */
public class ParCursoTF implements InterfaceHashExtensivel {
    
    private int idCurso;      // ID do curso (chave)
    private float tf;         // Term Frequency do termo neste curso
    private final short TAMANHO = 8;  // 4 bytes para int + 4 bytes para float

    public ParCursoTF() {
        this.idCurso = -1;
        this.tf = 0.0f;
    }

    public ParCursoTF(int idCurso, float tf) {
        this.idCurso = idCurso;
        this.tf = tf;
    }

    // Getters
    public int getIdCurso() {
        return idCurso;
    }

    public float getTF() {
        return tf;
    }

    // Setters
    public void setTF(float novoTF) {
        this.tf = novoTF;
    }

    @Override
    public int hashCode() {
        return this.idCurso;
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public String toString() {
        return "(" + this.idCurso + ";" + String.format("%.4f", this.tf) + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idCurso);
        dos.writeFloat(this.tf);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idCurso = dis.readInt();
        this.tf = dis.readFloat();
    }
}
