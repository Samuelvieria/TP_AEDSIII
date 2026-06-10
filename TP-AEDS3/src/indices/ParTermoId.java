package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// Par (termo, ID do curso, TF) usado nas listas invertidas do índice de busca por palavras-chave
public class ParTermoId implements InterfaceArvoreBMais<ParTermoId> {

    private String termo;
    private int idCurso;
    private float tf; // frequência do termo no nome do curso (Term Frequency)

    private final short TAMANHO_TERMO = 24; // 24 bytes para o termo
    private final short TAMANHO = TAMANHO_TERMO + 4 + 4; // termo + id + tf

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public ParTermoId() throws Exception {
        this("", -1, 0f);
    }

    public ParTermoId(String termo, int idCurso, float tf) throws Exception {
        // Termos muito longos são truncados para caber no registro de tamanho fixo
        if (termo.getBytes().length > TAMANHO_TERMO)
            termo = termo.substring(0, TAMANHO_TERMO);
        this.termo = termo;
        this.idCurso = idCurso;
        this.tf = tf;
    }

    public String getTermo() {
        return termo;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public float getTf() {
        return tf;
    }

    @Override
    public ParTermoId clone() {
        try {
            return new ParTermoId(this.termo, this.idCurso, this.tf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public int compareTo(ParTermoId o) {
        int comp = this.termo.compareTo(o.termo);
        if (comp != 0)
            return comp;

        // id == -1 indica uma chave de busca: casa com qualquer ID do mesmo termo
        if (this.idCurso == -1 || o.idCurso == -1)
            return 0;

        return Integer.compare(this.idCurso, o.idCurso);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Escreve o termo como array de bytes de tamanho fixo
        byte[] vb = new byte[TAMANHO_TERMO];
        byte[] vbTermo = this.termo.getBytes();
        int i = 0;
        while (i < vbTermo.length) {
            vb[i] = vbTermo[i];
            i++;
        }
        while (i < TAMANHO_TERMO) {
            vb[i] = ' ';
            i++;
        }
        dos.write(vb);
        dos.writeInt(this.idCurso);
        dos.writeFloat(this.tf);

        if (DEBUG) System.out.println("ParTermoId serializado: " + this.toString());
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        byte[] vb = new byte[TAMANHO_TERMO];
        dis.read(vb);
        this.termo = (new String(vb)).trim();
        this.idCurso = dis.readInt();
        this.tf = dis.readFloat();

        if (DEBUG) System.out.println("ParTermoId desserializado: " + this.toString());
    }

    @Override
    public String toString() {
        return "(" + this.termo + ";" + this.idCurso + ";" + this.tf + ")";
    }
}
