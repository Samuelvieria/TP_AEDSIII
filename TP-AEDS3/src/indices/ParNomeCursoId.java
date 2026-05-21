package indices;

import aed3.InterfaceArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class ParNomeCursoId implements InterfaceArvoreBMais<ParNomeCursoId> {

    private String nome;
    private int id;
    private final short TAMANHO = 30; // 26 bytes para o nome + 4 bytes para o id

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    public ParNomeCursoId() throws Exception {
        this("", -1);
    }

    public ParNomeCursoId(String nome) throws Exception {
        this(nome, -1);
    }

    public ParNomeCursoId(String nome, int id) throws Exception {
        if (nome.getBytes().length > 26)
            throw new Exception("Nome do curso muito extenso. Máximo de 26 caracteres.");
        this.nome = nome;
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    @Override
    public ParNomeCursoId clone() {
        try {
            return new ParNomeCursoId(this.nome, this.id);
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
    public int compareTo(ParNomeCursoId o) {
        String str1 = transforma(this.nome);
        String str2 = transforma(o.nome);

        // Para buscas por prefixo (quando id == -1)
        if (this.id == -1 && str2.length() > str1.length())
            str2 = str2.substring(0, str1.length());

        int comp = str1.compareTo(str2);
        if (comp != 0) return comp;

        // Se os nomes são iguais, desempata pelo ID
        if (this.id == -1) return 0;
        return Integer.compare(this.id, o.id);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Escreve o nome como array de bytes de tamanho fixo (26 bytes)
        byte[] vb = new byte[26];
        byte[] vbNome = this.nome.getBytes();
        int i = 0;
        while (i < vbNome.length) {
            vb[i] = vbNome[i];
            i++;
        }
        while (i < 26) {
            vb[i] = ' ';
            i++;
        }
        dos.write(vb);
        dos.writeInt(this.id);

        if (DEBUG) System.out.println("ParNomeCursoId serializado: " + this.toString());
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        byte[] vb = new byte[26];
        dis.read(vb);
        this.nome = (new String(vb)).trim();
        this.id = dis.readInt();

        if (DEBUG) System.out.println("ParNomeCursoId desserializado: " + this.toString());
    }

    @Override
    public String toString() {
        return this.nome + ";" + this.id;
    }

    // Método auxiliar para comparação ignorando acentos e case
    public static String transforma(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase();
    }
}