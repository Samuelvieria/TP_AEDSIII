package indices;

import aed3.RegistroHashExtensivel;
import java.io.*;

public class ParCodigoId implements RegistroHashExtensivel<ParCodigoId> {

    private int id;
    private long endereco;

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
        return id;
    }

    @Override
    public short size() {
        return 12;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(ba);

        dos.writeInt(id);
        dos.writeLong(endereco);

        return ba.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bi = new ByteArrayInputStream(ba);
        DataInputStream di = new DataInputStream(bi);

        id = di.readInt();
        endereco = di.readLong();
    }
}