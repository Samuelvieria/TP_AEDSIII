package visao;

import java.util.Scanner;
import entidades.Usuario;

public class VisaoUsuario {

private Scanner console;

public VisaoUsuario(Scanner console) {
    this.console = console;
}

public String menuAutenticacao() {
    System.out.println("\n\nEntrePares 1.0");
    System.out.println(    "--------------");
    System.out.println("\n> Início\n");
    System.out.println("(A) Login");
    System.out.println("(B) Novo usuário");
    System.out.println("(C) Recuperar senha");
    System.out.println("(S) Sair");
    System.out.print("\nOpção: ");
    return console.nextLine().trim().toUpperCase();
}

public String menuMeusDados() {
    System.out.println("\n\nEntrePares 1.0");
    System.out.println(    "--------------");
    System.out.println("\n> Início > Meus dados\n");
    System.out.println("(A) Exibir meus dados");
    System.out.println("(B) Alterar meus dados");
    System.out.println("(C) Alterar minha senha");
    System.out.println("(D) Excluir minha conta");
    System.out.println("(R) Retornar");
    System.out.print("\nOpção: ");
    return console.nextLine().trim().toUpperCase();
}

public String lerLinha(String campo) {
    System.out.print(campo);
    return console.nextLine().trim();
}

public boolean confirmar(String mensagem) {
    System.out.print(mensagem + " (S/N) ? ");
    String resposta = console.nextLine().trim().toUpperCase();
    return resposta.length() > 0 && resposta.charAt(0) == 'S';
}

public void mostrarMensagem(String mensagem) {
    System.out.println(mensagem);
}

public void mostrarUsuario(Usuario u) {
    if (u == null) {
        System.out.println("Usuário não encontrado.");
        return;
    }
    System.out.println("\nDADOS DO USUÁRIO");
    System.out.println("ID........: " + u.getID());
    System.out.println("Nome......: " + u.getNome());
    System.out.println("Email.....: " + u.getEmail());
    System.out.println("Pergunta..: " + u.getPerguntaSecreta());
}
}