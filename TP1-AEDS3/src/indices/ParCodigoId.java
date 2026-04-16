package indices;

import aed3.InterfaceHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Par (código compartilhável, ID do curso) para índice hash extensível
public class ParCodigoId implements InterfaceHashExtensivel {

    private String codigo; // chave: código NanoID de 10 caracteres
    private int idCurso; // valor: ID do curso
    private final short TAMANHO = 14; // 10 bytes para código + 4 bytes para int

    private static final boolean DEBUG = false;

    public ParCodigoId() {
        this.codigo = "";
        this.idCurso = -1;
    }

    public ParCodigoId(String codigo, int idCurso) throws Exception {
        if (codigo == null || codigo.length() != 10)
            throw new Exception("Código deve ter exatamente 10 caracteres");
        this.codigo = codigo;
        this.idCurso = idCurso;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getIdCurso() {
        return idCurso;
    }

    @Override
    public int hashCode() {
        // A chave é o código (hash calculado a partir da string)
        return Math.abs(codigo.hashCode());
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Escreve o código como array fixo de 10 bytes
        byte[] codBytes = codigo.getBytes(StandardCharsets.UTF_8);
        byte[] buffer = new byte[10];
        System.arraycopy(codBytes, 0, buffer, 0, Math.min(codBytes.length, 10));
        dos.write(buffer);
        dos.writeInt(idCurso);

        if (DEBUG)
            System.out.println("ParCodigoId serializado: " + toString());
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        byte[] buffer = new byte[10];
        dis.read(buffer);
        int len = 0;
        while (len < 10 && buffer[len] != 0)
            len++;
        codigo = new String(buffer, 0, len, StandardCharsets.UTF_8);
        idCurso = dis.readInt();

        if (DEBUG)
            System.out.println("ParCodigoId desserializado: " + toString());
    }

    @Override
    public String toString() {
        return "(" + codigo + ";" + idCurso + ")";
    }

}
