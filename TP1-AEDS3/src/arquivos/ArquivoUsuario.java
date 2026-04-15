package arquivos;

import entidades.Usuario;
import indices.ParEmailId;

import java.io.IOException;

import aed3.Arquivo;
import aed3.HashExtensivel;

public class ArquivoUsuario extends Arquivo<Usuario> {
    
    private HashExtensivel<ParEmailId> indiceEmail;
    
    // Constante para debug (mude para true para ativar logs)
    private static final boolean DEBUG = false;
    
    public ArquivoUsuario() throws Exception {
        super("usuario", Usuario.class.getConstructor());
        
        try {
            indiceEmail = new HashExtensivel<>(
                ParEmailId.class.getConstructor(),
                4,  // tamanho do cesto
                "./dados/usuario/indiceEmail.d.db",
                "./dados/usuario/indiceEmail.c.db"
            );
        } catch (Exception e) {
            System.err.println("Erro ao inicializar índice de email: " + e.getMessage());
            throw e;
        }
        
        if (DEBUG) System.out.println("ArquivoUsuario inicializado com sucesso.");
    }
    
    // ------------- CRUD -------------
    @Override
    public int create(Usuario u) throws Exception {
        // Validações
        if (u == null) throw new IllegalArgumentException("Usuário não pode ser nulo");
        if (u.getEmail() == null || u.getEmail().isEmpty()) {
            throw new Exception("Email do usuário é obrigatório");
        }
        
        // Verifica duplicidade de email
        Usuario existente = readEmail(u.getEmail());
        if (existente != null) {
            throw new Exception("Email já cadastrado: " + u.getEmail());
        }
        
        int id = super.create(u);
        if (DEBUG) System.out.println("Usuário criado com ID: " + id);
        
        try {
            indiceEmail.create(new ParEmailId(u.getEmail(), id));
            if (DEBUG) System.out.println("Índice de email atualizado.");
        } catch (Exception e) {
            //tenta remover o registro recem criado
            super.delete(id);
            throw new Exception("Falha ao criar índice de email: " + e.getMessage(), e);
        }
        
        return id;
    }
    
    @Override
    public Usuario read(int id) throws Exception {
        if (DEBUG) System.out.println("Buscando usuário por ID: " + id);
        return super.read(id);
    }
    
    public Usuario readEmail(String email) throws Exception {
        if (email == null || email.isEmpty()) return null;
        
        int hashCode = Math.abs(email.hashCode());
        if (DEBUG) System.out.println("Buscando email '" + email + "' com hash: " + hashCode);
        
        ParEmailId pei = indiceEmail.read(hashCode);
        if (pei == null) {
            if (DEBUG) System.out.println("Nenhum ParEmailId encontrado para hash.");
            return null;
        }
        
        // Confirma que é exatamente o email (colisão de hash pode ocorrer)
        if (!pei.getEmail().equals(email)) {
            if (DEBUG) System.out.println("Colisão de hash ou email diferente: " + pei.getEmail());
            return null;
        }
        
        return super.read(pei.getId());
    }
    
    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        if (novoUsuario == null) return false;
        
        Usuario antigo = super.read(novoUsuario.getID());
        if (antigo == null) {
            if (DEBUG) System.out.println("Usuário não encontrado para update: ID " + novoUsuario.getID());
            return false;
        }
        
        // Verifica mudança de email
        boolean emailAlterado = !antigo.getEmail().equals(novoUsuario.getEmail());
        if (emailAlterado) {
            // Novo email não pode já existir para outro usuário
            Usuario existente = readEmail(novoUsuario.getEmail());
            if (existente != null && existente.getID() != novoUsuario.getID()) {
                throw new Exception("Novo email já está em uso por outro usuário");
            }
            
            // Remove índice antigo
            indiceEmail.delete(Math.abs(antigo.getEmail().hashCode()));
            if (DEBUG) System.out.println("Índice antigo removido: " + antigo.getEmail());
        }
        
        boolean atualizado = super.update(novoUsuario);
        if (!atualizado) {
            // Rollback do índice
            if (emailAlterado) {
                indiceEmail.create(new ParEmailId(antigo.getEmail(), antigo.getID()));
            }
            return false;
        }
        
        if (emailAlterado) {
            indiceEmail.create(new ParEmailId(novoUsuario.getEmail(), novoUsuario.getID()));
            if (DEBUG) System.out.println("Novo índice criado: " + novoUsuario.getEmail());
        }
        
        return true;
    }
    
    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = super.read(id);
        if (u == null) {
            if (DEBUG) System.out.println("Tentativa de excluir usuário inexistente ID: " + id);
            return false;
        }
        
        // Verificar se possui cursos ativos
        if (possuiCursosAtivos(id)) {
            throw new Exception("Usuário possui cursos ativos e não pode ser excluído");
        }
        
        // Remove índice de email
        boolean indiceRemovido = indiceEmail.delete(Math.abs(u.getEmail().hashCode()));
        if (DEBUG) System.out.println("Índice de email removido: " + indiceRemovido);
        
        // Exclusão lógica no arquivo de dados
        return super.delete(id);
    }
    
    // ------------- MÉTODOS DE AUTENTICAÇÃO -------------
    public Usuario login(String email, String senha) throws Exception {
        Usuario u = readEmail(email);
        if (u != null && u.verificaSenha(senha)) {
            if (DEBUG) System.out.println("Login bem-sucedido para: " + email);
            return u;
        }
        if (DEBUG) System.out.println("Falha no login para: " + email);
        return null;
    }
    
    public boolean recuperarSenha(String email, String resposta, String novaSenha) throws Exception {
        Usuario u = readEmail(email);
        if (u == null) return false;
        if (!u.verificaResposta(resposta)) return false;
        
        u.setSenha(novaSenha);
        return update(u);
    }
    
    public boolean emailExiste(String email) throws Exception {
        return readEmail(email) != null;
    }
    
    // ------------- MÉTODO AUXILIAR -------------
    private boolean possuiCursosAtivos(int idUsuario) {
        //Retorna false para permitir exclusão
        return false;
    }
    
    // ------------- FECHAMENTO -------------
    @Override
    public void close() throws Exception {
        try {
            super.close();
            if (indiceEmail != null) indiceEmail.close();
            if (DEBUG) System.out.println("ArquivoUsuario fechado corretamente.");
        } catch (IOException e) {
            System.err.println("Erro ao fechar ArquivoUsuario: " + e.getMessage());
            throw e;
        }
    }
}