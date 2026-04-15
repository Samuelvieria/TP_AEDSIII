package controle;

import java.util.Scanner;

import arquivos.ArquivoUsuario;
import entidades.Usuario;
import visao.VisaoUsuario;

public class ControleUsuario {

private ArquivoUsuario arqUsuarios;
private VisaoUsuario visao;

public ControleUsuario(Scanner console) throws Exception {
    this.arqUsuarios = new ArquivoUsuario();
    this.visao = new VisaoUsuario(console);
}

// Menu inicial: login, cadastro, recuperação
public boolean menuAutenticacao() throws Exception {
    String opcao;
    
    do {
        opcao = visao.menuAutenticacao();
        
        switch (opcao) {
            case "A":
                if (login())
                    return true;
                break;
                
            case "B":
                cadastrarUsuario();
                break;
                
            case "C":
                recuperarSenha();
                break;
                
            case "S":
                return false;
                
            default:
                visao.mostrarMensagem("Opção inválida.");
        }
        
    } while (true);
}

// Menu de meus dados, já autenticado
public void menuMeusDados() throws Exception {
    if (!Sessao.temUsuarioLogado()) {
        visao.mostrarMensagem("Nenhum usuário logado.");
        return;
    }
    
    String opcao;
    do {
        opcao = visao.menuMeusDados();
        
        switch (opcao) {
            case "A":
                exibirMeusDados();
                break;
                
            case "B":
                alterarMeusDados();
                break;
                
            case "C":
                alterarMinhaSenha();
                break;
                
            case "D":
                if (excluirMinhaConta())
                    return;
                break;
                
            case "R":
                break;
                
            default:
                visao.mostrarMensagem("Opção inválida.");
        }
        
    } while (!opcao.equals("R"));
}

private boolean login() throws Exception {
    System.out.println("\nLOGIN");
    
    String email = visao.lerLinha("E-mail: ");
    if (email.length() == 0)
        return false;
    
    String senha = visao.lerLinha("Senha: ");
    if (senha.length() == 0)
        return false;
    
    Usuario u = arqUsuarios.login(email, senha);
    
    if (u != null) {
        Sessao.iniciar(u);
        visao.mostrarMensagem("Login realizado com sucesso!");
        return true;
    }
    
    visao.mostrarMensagem("E-mail ou senha inválidos.");
    return false;
}

private void cadastrarUsuario() throws Exception {
    System.out.println("\nNOVO USUÁRIO");
    
    try {
        String nome = visao.lerLinha("Nome: ");
        if (nome.length() == 0)
            return;
        
        String email = visao.lerLinha("E-mail: ");
        if (email.length() == 0)
            return;
        
        if (arqUsuarios.emailExiste(email)) {
            visao.mostrarMensagem("Já existe usuário com esse e-mail.");
            return;
        }
        
        String senha = visao.lerLinha("Senha: ");
        if (senha.length() == 0)
            return;
        
        String confirmaSenha = visao.lerLinha("Confirmar senha: ");
        if (!senha.equals(confirmaSenha)) {
            visao.mostrarMensagem("As senhas não conferem.");
            return;
        }
        
        String pergunta = visao.lerLinha("Pergunta secreta: ");
        if (pergunta.length() == 0)
            return;
        
        String resposta = visao.lerLinha("Resposta secreta: ");
        if (resposta.length() == 0)
            return;
        
        Usuario u = new Usuario(nome, email, senha, pergunta, resposta);
        
        if (visao.confirmar("Confirmar cadastro")) {
            arqUsuarios.create(u);
            visao.mostrarMensagem("Usuário cadastrado com sucesso!");
        }
        
    } catch (Exception e) {
        visao.mostrarMensagem("Erro no cadastro: " + e.getMessage());
    }
}

private void exibirMeusDados() throws Exception {
    Usuario u = arqUsuarios.read(Sessao.getIdUsuarioLogado());
    visao.mostrarUsuario(u);
}

private void alterarMeusDados() throws Exception {
    Usuario u = arqUsuarios.read(Sessao.getIdUsuarioLogado());
    
    if (u == null) {
        visao.mostrarMensagem("Usuário não encontrado.");
        return;
    }
    
    System.out.println("\nALTERAÇÃO DE DADOS");
    System.out.println("Deixe o campo em branco se não quiser alterar.");
    
    try {
        String novoNome = visao.lerLinha("Nome [" + u.getNome() + "]: ");
        if (novoNome.length() > 0)
            u.setNome(novoNome);
        
        String novoEmail = visao.lerLinha("E-mail [" + u.getEmail() + "]: ");
        if (novoEmail.length() > 0 && !novoEmail.equals(u.getEmail())) {
            Usuario outro = arqUsuarios.readEmail(novoEmail);
            if (outro != null && outro.getID() != u.getID()) {
                visao.mostrarMensagem("Já existe outro usuário com esse e-mail.");
                return;
            }
            u.setEmail(novoEmail);
        }
        
        String novaPergunta = visao.lerLinha("Pergunta secreta [" + u.getPerguntaSecreta() + "]: ");
        if (novaPergunta.length() > 0)
            u.setPerguntaSecreta(novaPergunta);
        
        String novaResposta = visao.lerLinha("Nova resposta secreta [ENTER para manter]: ");
        if (novaResposta.length() > 0)
            u.setResposta(novaResposta);
        
        if (visao.confirmar("Confirmar alteração")) {
            if (arqUsuarios.update(u)) {
                Sessao.iniciar(u);
                visao.mostrarMensagem("Dados alterados com sucesso!");
            } else {
                visao.mostrarMensagem("Erro ao alterar dados.");
            }
        }
        
    } catch (Exception e) {
        visao.mostrarMensagem("Erro na alteração: " + e.getMessage());
    }
}

private void alterarMinhaSenha() throws Exception {
    Usuario u = arqUsuarios.read(Sessao.getIdUsuarioLogado());
    
    if (u == null) {
        visao.mostrarMensagem("Usuário não encontrado.");
        return;
    }
    
    String senhaAtual = visao.lerLinha("Senha atual: ");
    if (!u.verificaSenha(senhaAtual)) {
        visao.mostrarMensagem("Senha atual incorreta.");
        return;
    }
    
    String novaSenha = visao.lerLinha("Nova senha: ");
    if (novaSenha.length() == 0)
        return;
    
    String confirma = visao.lerLinha("Confirmar nova senha: ");
    if (!novaSenha.equals(confirma)) {
        visao.mostrarMensagem("As senhas não conferem.");
        return;
    }
    
    try {
        u.setSenha(novaSenha);
        
        if (visao.confirmar("Confirmar alteração de senha")) {
            if (arqUsuarios.update(u)) {
                Sessao.iniciar(u);
                visao.mostrarMensagem("Senha alterada com sucesso!");
            } else {
                visao.mostrarMensagem("Erro ao alterar senha.");
            }
        }
        
    } catch (Exception e) {
        visao.mostrarMensagem("Erro ao alterar senha: " + e.getMessage());
    }
}

private void recuperarSenha() throws Exception {
    System.out.println("\nRECUPERAÇÃO DE SENHA");
    
    String email = visao.lerLinha("E-mail: ");
    if (email.length() == 0)
        return;
    
    Usuario u = arqUsuarios.readEmail(email);
    if (u == null) {
        visao.mostrarMensagem("Usuário não encontrado.");
        return;
    }
    
    visao.mostrarMensagem("Pergunta secreta: " + u.getPerguntaSecreta());
    String resposta = visao.lerLinha("Resposta: ");
    if (resposta.length() == 0)
        return;
    
    String novaSenha = visao.lerLinha("Nova senha: ");
    if (novaSenha.length() == 0)
        return;
    
    String confirma = visao.lerLinha("Confirmar nova senha: ");
    if (!novaSenha.equals(confirma)) {
        visao.mostrarMensagem("As senhas não conferem.");
        return;
    }
    
    try {
        if (arqUsuarios.recuperarSenha(email, resposta, novaSenha))
            visao.mostrarMensagem("Senha redefinida com sucesso!");
        else
            visao.mostrarMensagem("Resposta secreta inválida.");
    } catch (Exception e) {
        visao.mostrarMensagem("Erro na recuperação de senha: " + e.getMessage());
    }
}

private boolean excluirMinhaConta() throws Exception {
    Usuario u = arqUsuarios.read(Sessao.getIdUsuarioLogado());
    
    if (u == null) {
        visao.mostrarMensagem("Usuário não encontrado.");
        return false;
    }
    
    visao.mostrarUsuario(u);
    
    if (!visao.confirmar("Confirma exclusão da conta"))
        return false;
    
    try {
        if (arqUsuarios.delete(u.getID())) {
            Sessao.encerrar();
            visao.mostrarMensagem("Conta excluída com sucesso!");
            return true;
        } else {
            visao.mostrarMensagem("Não foi possível excluir a conta.");
        }
    } catch (Exception e) {
        visao.mostrarMensagem("Erro ao excluir conta: " + e.getMessage());
    }
    
    return false;
}

public void logout() {
    Sessao.encerrar();
    visao.mostrarMensagem("Logout realizado.");
}

public void close() throws Exception {
    arqUsuarios.close();
}
}