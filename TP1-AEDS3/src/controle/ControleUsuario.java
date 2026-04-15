package controle;

import arquivos.ArquivoUsuario;
import entidades.Usuario;
import visao.VisaoUsuario;

import java.util.Scanner;

// CLASSE DE CONTROLE PARA USUÁRIO
// Responsável pela lógica de autenticação e gerenciamento dos dados do usuário.
// Orquestra a interação entre a visão (VisaoUsuario) e o modelo (ArquivoUsuario).
public class ControleUsuario {

    private ArquivoUsuario arqUsuario;
    private VisaoUsuario visao;

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    // Construtor recebe o Scanner (para a visão) e inicializa o arquivo de usuários
    public ControleUsuario(Scanner console) throws Exception {
        this.visao = new VisaoUsuario(console);
        this.arqUsuario = new ArquivoUsuario();
        if (DEBUG) System.out.println("[DEBUG] ControleUsuario inicializado.");
    }

    // ------------------------------------------------------------------------------
    // FLUXO PRINCIPAL DE AUTENTICAÇÃO
    // ------------------------------------------------------------------------------

    // Exibe o menu de autenticação e direciona para login, cadastro ou recuperação.
    // Retorna true se o usuário logou com sucesso, false se saiu.
    public boolean autenticar() {
        if (DEBUG) System.out.println("[DEBUG] Iniciando fluxo de autenticação.");
        String opcao;
        do {
            opcao = visao.menuAutenticacao();
            if (DEBUG) System.out.println("[DEBUG] Opção escolhida: " + opcao);
            switch (opcao) {
                case "A":
                    if (login()) return true;
                    break;
                case "B":
                    cadastrar();
                    break;
                case "C":
                    recuperarSenha();
                    break;
                case "S":
                    if (DEBUG) System.out.println("[DEBUG] Usuário escolheu sair.");
                    return false;
                default:
                    visao.mostrarMensagem("Opção inválida.");
            }
        } while (!opcao.equals("S"));
        return false;
    }

    // Fluxo de login: pede email e senha, valida no arquivo.
    // Se bem-sucedido, registra o usuário na sessão e retorna true.
    private boolean login() {
        if (DEBUG) System.out.println("[DEBUG] Iniciando login.");
        String email = visao.lerEmail();
        String senha = visao.lerSenha();

        try {
            Usuario u = arqUsuario.login(email, senha);
            if (u != null) {
                Sessao.setUsuario(u);
                if (DEBUG) System.out.println("[DEBUG] Login bem-sucedido para: " + email);
                visao.mostrarMensagem("Login realizado com sucesso! Bem-vindo, " + u.getNome() + ".");
                return true;
            } else {
                if (DEBUG) System.out.println("[DEBUG] Falha no login para: " + email);
                visao.mostrarMensagem("Email ou senha incorretos.");
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Exceção no login: " + e.getMessage());
            visao.mostrarMensagem("Erro ao realizar login: " + e.getMessage());
        }
        return false;
    }

    // Fluxo de cadastro de novo usuário.
    private void cadastrar() {
        if (DEBUG) System.out.println("[DEBUG] Iniciando cadastro.");
        visao.mostrarMensagem("\n--- NOVO CADASTRO ---");
        String nome = visao.lerNome();
        String email = visao.lerEmail();

        // Verifica se o email já existe antes de continuar
        try {
            if (arqUsuario.emailExiste(email)) {
                if (DEBUG) System.out.println("[DEBUG] Email já cadastrado: " + email);
                visao.mostrarMensagem("Email já cadastrado. Tente fazer login ou use outro email.");
                return;
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Erro ao verificar email: " + e.getMessage());
            visao.mostrarMensagem("Erro ao verificar email: " + e.getMessage());
            return;
        }

        String senha = visao.lerSenha();
        String pergunta = visao.lerPerguntaSecreta();
        String resposta = visao.lerRespostaSecreta();

        if (!visao.confirmar("Confirmar cadastro")) {
            if (DEBUG) System.out.println("[DEBUG] Cadastro cancelado pelo usuário.");
            visao.mostrarMensagem("Cadastro cancelado.");
            return;
        }

        Usuario novo = new Usuario(nome, email, senha, pergunta, resposta);
        try {
            int id = arqUsuario.create(novo);
            if (DEBUG) System.out.println("[DEBUG] Usuário criado com ID: " + id);
            visao.mostrarMensagem("Usuário cadastrado com sucesso! Seu ID é " + id + ".");
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Erro no create: " + e.getMessage());
            visao.mostrarMensagem("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    // Fluxo de recuperação de senha: email, resposta secreta, nova senha.
    private void recuperarSenha() {
        if (DEBUG) System.out.println("[DEBUG] Iniciando recuperação de senha.");
        visao.mostrarMensagem("\n--- RECUPERAÇÃO DE SENHA ---");
        String email = visao.lerEmail();
        String resposta = visao.lerRespostaSecreta();

        try {
            // Verifica se o email existe
            if (!arqUsuario.emailExiste(email)) {
                if (DEBUG) System.out.println("[DEBUG] Email não encontrado: " + email);
                visao.mostrarMensagem("Email não encontrado.");
                return;
            }

            String novaSenha = visao.lerSenha();
            if (arqUsuario.recuperarSenha(email, resposta, novaSenha)) {
                if (DEBUG) System.out.println("[DEBUG] Senha alterada com sucesso para: " + email);
                visao.mostrarMensagem("Senha alterada com sucesso! Faça login com a nova senha.");
            } else {
                if (DEBUG) System.out.println("[DEBUG] Resposta secreta incorreta para: " + email);
                visao.mostrarMensagem("Resposta secreta incorreta.");
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Erro na recuperação: " + e.getMessage());
            visao.mostrarMensagem("Erro na recuperação: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------------------
    // MENU "MEUS DADOS" (após login)
    // ------------------------------------------------------------------------------

    // Exibe o menu de gerenciamento dos dados do usuário logado.
    // O usuário deve estar autenticado (Sessao.isLogado() == true).
    public void menuMeusDados() {
        if (!Sessao.isLogado()) {
            if (DEBUG) System.out.println("[DEBUG] Tentativa de acessar Meus Dados sem login.");
            visao.mostrarMensagem("Nenhum usuário logado.");
            return;
        }
        if (DEBUG) System.out.println("[DEBUG] Acessando menu Meus Dados para usuário ID: " + Sessao.getIdUsuarioLogado());

        String opcao;
        do {
            opcao = visao.menuMeusDados();
            if (DEBUG) System.out.println("[DEBUG] Opção Meus Dados: " + opcao);
            switch (opcao) {
                case "A":
                    exibirDados();
                    break;
                case "B":
                    alterarDados();
                    break;
                case "C":
                    alterarSenha();
                    break;
                case "D":
                    excluirConta();
                    if (!Sessao.isLogado()) {
                        if (DEBUG) System.out.println("[DEBUG] Conta excluída, saindo do menu.");
                        return;
                    }
                    break;
                case "R":
                    if (DEBUG) System.out.println("[DEBUG] Retornando do menu Meus Dados.");
                    break;
                default:
                    visao.mostrarMensagem("Opção inválida.");
            }
        } while (!opcao.equals("R"));
    }

    // Exibe os dados do usuário logado.
    private void exibirDados() {
        Usuario u = Sessao.getUsuario();
        visao.mostrarUsuario(u);
        if (DEBUG) System.out.println("[DEBUG] Dados exibidos para usuário ID: " + u.getID());
    }

    // Permite alterar nome e email do usuário logado.
    private void alterarDados() {
        Usuario u = Sessao.getUsuario();
        visao.mostrarUsuarioResumido(u);
        if (DEBUG) System.out.println("[DEBUG] Iniciando alteração de dados para ID: " + u.getID());

        String novoNome = visao.lerLinha("Novo nome (deixe em branco para manter): ");
        String novoEmail = visao.lerLinha("Novo email (deixe em branco para manter): ");

        if (novoNome.isEmpty() && novoEmail.isEmpty()) {
            if (DEBUG) System.out.println("[DEBUG] Nenhuma alteração solicitada.");
            visao.mostrarMensagem("Nenhuma alteração realizada.");
            return;
        }

        if (!novoNome.isEmpty()) {
            u.setNome(novoNome);
            if (DEBUG) System.out.println("[DEBUG] Nome alterado para: " + novoNome);
        }

        if (!novoEmail.isEmpty()) {
            try {
                if (!novoEmail.equals(u.getEmail()) && arqUsuario.emailExiste(novoEmail)) {
                    if (DEBUG) System.out.println("[DEBUG] Novo email já existe: " + novoEmail);
                    visao.mostrarMensagem("Este email já está em uso por outro usuário.");
                    return;
                }
                u.setEmail(novoEmail);
                if (DEBUG) System.out.println("[DEBUG] Email alterado para: " + novoEmail);
            } catch (Exception e) {
                if (DEBUG) System.out.println("[DEBUG] Erro ao verificar email: " + e.getMessage());
                visao.mostrarMensagem("Erro ao verificar email: " + e.getMessage());
                return;
            }
        }

        if (!visao.confirmar("Confirmar alterações")) {
            if (DEBUG) System.out.println("[DEBUG] Alterações canceladas pelo usuário.");
            visao.mostrarMensagem("Alterações canceladas.");
            return;
        }

        try {
            if (arqUsuario.update(u)) {
                Sessao.setUsuario(u); // atualiza a sessão com os novos dados
                if (DEBUG) System.out.println("[DEBUG] Usuário atualizado com sucesso.");
                visao.mostrarMensagem("Dados atualizados com sucesso.");
            } else {
                if (DEBUG) System.out.println("[DEBUG] Falha no update.");
                visao.mostrarMensagem("Erro ao atualizar os dados.");
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Exceção no update: " + e.getMessage());
            visao.mostrarMensagem("Erro: " + e.getMessage());
        }
    }

    // Permite alterar a senha (exige senha atual).
    private void alterarSenha() {
        Usuario u = Sessao.getUsuario();
        if (DEBUG) System.out.println("[DEBUG] Iniciando alteração de senha para ID: " + u.getID());

        String senhaAtual = visao.lerLinha("Senha atual: ");
        if (!u.verificaSenha(senhaAtual)) {
            if (DEBUG) System.out.println("[DEBUG] Senha atual incorreta.");
            visao.mostrarMensagem("Senha atual incorreta.");
            return;
        }

        String novaSenha = visao.lerSenha();
        u.setSenha(novaSenha);

        try {
            if (arqUsuario.update(u)) {
                Sessao.setUsuario(u);
                if (DEBUG) System.out.println("[DEBUG] Senha alterada com sucesso.");
                visao.mostrarMensagem("Senha alterada com sucesso.");
            } else {
                if (DEBUG) System.out.println("[DEBUG] Falha ao atualizar senha.");
                visao.mostrarMensagem("Erro ao alterar a senha.");
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Exceção na alteração de senha: " + e.getMessage());
            visao.mostrarMensagem("Erro: " + e.getMessage());
        }
    }

    // Exclui a conta do usuário logado, após confirmação e verificação de cursos ativos.
    private void excluirConta() {
        Usuario u = Sessao.getUsuario();
        visao.mostrarUsuarioResumido(u);
        if (DEBUG) System.out.println("[DEBUG] Iniciando exclusão de conta para ID: " + u.getID());

        if (!visao.confirmar("ATENÇÃO: Isso excluirá permanentemente sua conta. Continuar")) {
            if (DEBUG) System.out.println("[DEBUG] Exclusão cancelada pelo usuário.");
            visao.mostrarMensagem("Exclusão cancelada.");
            return;
        }

        try {
            if (arqUsuario.delete(u.getID())) {
                if (DEBUG) System.out.println("[DEBUG] Conta excluída com sucesso.");
                visao.mostrarMensagem("Conta excluída com sucesso.");
                Sessao.logout();
            } else {
                if (DEBUG) System.out.println("[DEBUG] Falha na exclusão (delete retornou false).");
                visao.mostrarMensagem("Não foi possível excluir a conta.");
            }
        } catch (Exception e) {
            if (DEBUG) System.out.println("[DEBUG] Exceção na exclusão: " + e.getMessage());
            visao.mostrarMensagem("Erro ao excluir conta: " + e.getMessage());
        }
    }

    // Fecha o arquivo de usuários (deve ser chamado ao encerrar o programa)
    public void close() throws Exception {
        if (DEBUG) System.out.println("[DEBUG] Fechando ControleUsuario.");
        arqUsuario.close();
    }
}