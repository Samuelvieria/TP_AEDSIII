package controle;

import entidades.Usuario;

// CLASSE DE SESSÃO
// Gerencia o usuário atualmente logado no sistema.
// Como o sistema é monousuário em execução, usamos atributos estáticos.
// Isso permite que qualquer controle saiba quem está logado.
public class Sessao {

    private static Usuario usuarioLogado = null;

    // Define o usuário logado (após login bem-sucedido)
    public static void setUsuario(Usuario u) {
        usuarioLogado = u;
    }

    // Retorna o usuário logado, ou null se ninguém estiver logado
    public static Usuario getUsuario() {
        return usuarioLogado;
    }

    // Verifica se há um usuário autenticado no momento
    public static boolean isLogado() {
        return usuarioLogado != null;
    }

    // Encerra a sessão (logout)
    public static void logout() {
        usuarioLogado = null;
    }

    // Retorna o ID do usuário logado, ou -1 se não houver
    public static int getIdUsuarioLogado() {
        return isLogado() ? usuarioLogado.getID() : -1;
    }
}