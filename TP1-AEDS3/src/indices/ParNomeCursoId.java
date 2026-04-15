package indices;

import aed3.RegistroArvoreBMais;
import java.io.*;

public class ParNomeCursoId implements RegistroArvoreBMais<ParNomeCursoId> {

    private String nome;
    private int id;

    public ParNomeCursoId() {
        this("", -1);
    }

    public ParNomeCursoId(String nome, int id) {
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
    public int compareTo(ParNomeCursoId o) {
        int comp = this.nome.compareTo(o.nome);
        if (comp != 0) return comp;
        return Integer.compare(this.id, o.id);
    }

    @Override
    public short size() {
        return 100;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(ba);

        dos.writeUTF(nome);
        dos.writeInt(id);

        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);

        nome = di.readUTF();
        id = di.readInt();
    }
}