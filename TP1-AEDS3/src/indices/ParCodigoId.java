package indices;

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParCodigoId implements InterfaceHashExtensivel {

    private int id;          // chave (ID do curso)
    private long endereco;   // valor (endereço no arquivo)
    private final short TAMANHO = 12;  // 4 bytes (int) + 8 bytes (long)

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public ParCodigoId() {
        this(-1, -1);
    }

    public ParCodigoId(int id, long endereco) {
        this.id = id;
        this.endereco = endereco;
    }

    public int getId() {
        return id;
    }

    public long getEndereco() {
        return endereco;
    }

    @Override
    public int hashCode() {
        // A chave é o ID (já é positivo, mas garantimos valor absoluto)
        return Math.abs(id);
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeLong(endereco);

        if (DEBUG) System.out.println("ParCodigoId serializado: " + this.toString());
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        id = dis.readInt();
        endereco = dis.readLong();

        if (DEBUG) System.out.println("ParCodigoId desserializado: " + this.toString());
    }

    @Override
    public String toString() {
        return "(" + id + ";" + endereco + ")";
    }
}