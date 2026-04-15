package visao;

import java.util.Scanner;
import entidades.Usuario;

// CLASSE DE VISÃO PARA USUÁRIO
// Responsável por toda interação com o usuário via terminal:
// - Exibição de menus
// - Leitura de dados (nome, email, senha, etc.)
// - Confirmações
// - Exibição de mensagens e dados do usuário
// Esta classe NÃO contém lógica de negócio. Ela apenas coleta e exibe informações.
public class VisaoUsuario {

    private Scanner console;

    // Construtor recebe o Scanner (normalmente System.in) para leitura do teclado
    public VisaoUsuario(Scanner console) {
        this.console = console;
    }

    // ------------------------------------------------------------------------------
    // MENUS
    // ------------------------------------------------------------------------------

    // Exibe o menu inicial de autenticação e retorna a opção escolhida.
    // Opções: (A) Login, (B) Novo usuário, (C) Recuperar senha, (S) Sair
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

    // Exibe o menu "Meus dados" (acessível após login) e retorna a opção escolhida.
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

    // ------------------------------------------------------------------------------
    // LEITURA DE DADOS ESPECÍFICOS (auxiliares para o ControleUsuario)
    // ------------------------------------------------------------------------------

    // Lê uma linha de texto genérica, exibindo o nome do campo como prompt.
    public String lerLinha(String campo) {
        System.out.print(campo);
        return console.nextLine().trim();
    }

    // Lê o nome do usuário (não pode ser vazio).
    public String lerNome() {
        String nome;
        do {
            System.out.print("Nome: ");
            nome = console.nextLine().trim();
            if (nome.isEmpty())
                System.out.println("Nome não pode ser vazio.");
        } while (nome.isEmpty());
        return nome;
    }

    // Lê o email do usuário (validação simples de formato).
    public String lerEmail() {
        String email;
        do {
            System.out.print("Email: ");
            email = console.nextLine().trim();
            if (email.isEmpty() || !email.contains("@") || !email.contains("."))
                System.out.println("Email inválido. Tente novamente.");
        } while (email.isEmpty() || !email.contains("@") || !email.contains("."));
        return email;
    }

    // Lê a senha (mínimo 4 caracteres).
    public String lerSenha() {
        String senha;
        do {
            System.out.print("Senha (mínimo 4 caracteres): ");
            senha = console.nextLine().trim();
            if (senha.length() < 4)
                System.out.println("Senha muito curta.");
        } while (senha.length() < 4);
        return senha;
    }

    // Lê a pergunta secreta (não pode ser vazia).
    public String lerPerguntaSecreta() {
        String pergunta;
        do {
            System.out.print("Pergunta secreta (ex.: Cidade natal?): ");
            pergunta = console.nextLine().trim();
            if (pergunta.isEmpty())
                System.out.println("A pergunta secreta não pode ser vazia.");
        } while (pergunta.isEmpty());
        return pergunta;
    }

    // Lê a resposta secreta (não pode ser vazia).
    public String lerRespostaSecreta() {
        String resposta;
        do {
            System.out.print("Resposta secreta: ");
            resposta = console.nextLine().trim();
            if (resposta.isEmpty())
                System.out.println("A resposta secreta não pode ser vazia.");
        } while (resposta.isEmpty());
        return resposta;
    }

    // ------------------------------------------------------------------------------
    // CONFIRMAÇÃO E MENSAGENS
    // ------------------------------------------------------------------------------

    // Exibe uma pergunta de confirmação e retorna true se o usuário responder 'S'.
    public boolean confirmar(String mensagem) {
        System.out.print(mensagem + " (S/N) ? ");
        String resposta = console.nextLine().trim().toUpperCase();
        return resposta.length() > 0 && resposta.charAt(0) == 'S';
    }

    // Exibe uma mensagem simples na tela.
    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    // ------------------------------------------------------------------------------
    // EXIBIÇÃO DE DADOS
    // ------------------------------------------------------------------------------

    // Exibe os dados de um usuário formatados.
    // Usado para mostrar o perfil do usuário logado.
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

    // Exibe os dados de um usuário de forma resumida (apenas nome e email).
    // Útil para confirmações rápidas.
    public void mostrarUsuarioResumido(Usuario u) {
        if (u == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }
        System.out.println("Usuário: " + u.getNome() + " (" + u.getEmail() + ")");
    }
}