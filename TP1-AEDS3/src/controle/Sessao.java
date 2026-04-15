package controle;

import entidades.Usuario;

public class Sessao {

private static Usuario usuarioLogado = null;

public static void iniciar(Usuario u) {
    usuarioLogado = u;
}

public static void encerrar() {
    usuarioLogado = null;
}

public static Usuario getUsuarioLogado() {
    return usuarioLogado;
}

public static boolean temUsuarioLogado() {
    return usuarioLogado != null;
}

public static int getIdUsuarioLogado() {
    if (usuarioLogado == null)
        return -1;
    return usuarioLogado.getID();
}
}