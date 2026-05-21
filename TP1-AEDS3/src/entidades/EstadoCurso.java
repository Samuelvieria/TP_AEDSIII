package entidades;

public enum EstadoCurso {
    ABERTO(0, "Aberto para inscrições"),
    ENCERRADO(1, "Inscrições encerradas"),
    REALIZADO(2, "Curso já realizado"),
    CANCELADO(3, "Curso cancelado");

    private final int codigo;
    private final String descricao;

    EstadoCurso(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static EstadoCurso fromCodigo(int codigo) {
        for (EstadoCurso estado : EstadoCurso.values()) {
            if (estado.getCodigo() == codigo) {
                return estado;
            }
        }
        return ABERTO; // Padrão de segurança
    }
}