package entidades;

import java.io.*;

public class Curso {

    private String code;
    private String name;
    private String description;
    private int credits;

    public Curso() {}

    public Curso(String code, String name, String description, int credits) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.credits = credits;
    }

    // GETTERS E SETTERS
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

  
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(code != null ? code : "");
        dos.writeUTF(name != null ? name : "");
        dos.writeUTF(description != null ? description : "");
        dos.writeInt(credits);

        return baos.toByteArray();
    }

    // 🔥 DESSERIALIZAÇÃO
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        code = dis.readUTF();
        name = dis.readUTF();
        description = dis.readUTF();
        credits = dis.readInt();
    }

    @Override
    public String toString() {
        return "Curso{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", credits=" + credits +
                '}';
    }
}