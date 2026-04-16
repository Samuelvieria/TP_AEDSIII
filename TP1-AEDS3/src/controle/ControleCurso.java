package controle;

import arquivos.ArquivoCurso;
import entidades.Curso;
import java.util.ArrayList;
import java.util.Scanner;
import visao.VisaoCurso;

// CLASSE DE CONTROLE PARA CURSO
// Responsável pela lógica de gerenciamento dos cursos do usuário logado.
// Orquestra a interação entre a visão (VisaoCurso) e o modelo (ArquivoCurso).
public class ControleCurso {

    private ArquivoCurso arqCurso;
    private VisaoCurso visao;

    // Constante para ativar/desativar logs de debug
    private static final boolean DEBUG = false;

    // Construtor recebe o Scanner (para a visão) e inicializa o arquivo de cursos
    public ControleCurso(Scanner console) throws Exception {
        this.visao = new VisaoCurso(console);
        this.arqCurso = new ArquivoCurso();
        if (DEBUG) System.out.println("[DEBUG] ControleCurso inicializado.");
    }

    // ------------------------------------------------------------------------------
    // MENU PRINCIPAL "MEUS CURSOS"
    // ------------------------------------------------------------------------------

    // Gerencia o fluxo principal da seção "Meus cursos".
    // Lista os cursos do usuário, permite selecionar um ou criar novo.
    public void gerenciarCursos() {
        if (DEBUG) System.out.println("[DEBUG] Acessando menu Meus Cursos.");
        String opcao;
        do {
            int idUsuarioLogado = Sessao.getIdUsuarioLogado();
            ArrayList<Curso> cursosUsuario = new ArrayList<>();

            // Carrega os cursos do usuário logado
            try {
                cursosUsuario = this.arqCurso.listarPorUsuario(idUsuarioLogado);
                if (cursosUsuario != null) {
                    // Ordena alfabeticamente por nome (ignorando maiúsculas/minúsculas)
                    cursosUsuario.sort((c1, c2) -> c1.getNome().compareToIgnoreCase(c2.getNome()));
                }
            } catch (Exception e) {
                this.visao.mostrarMensagem("Erro ao carregar cursos: " + e.getMessage());
                if (DEBUG) System.out.println("[DEBUG] Exceção ao listar cursos: " + e.getMessage());
            }

            // Exibe a lista numerada e o menu de opções
            this.visao.listarCursos(cursosUsuario);
            opcao = this.visao.menuCursos();

            if (opcao.equals("A")) {
                this.incluirCurso(idUsuarioLogado);
            } else if (opcao.matches("\\d+")) { // Verifica se é um número (índice do curso)
                int indice = Integer.parseInt(opcao) - 1;
                if (cursosUsuario != null && indice >= 0 && indice < cursosUsuario.size()) {
                    Curso selecionado = cursosUsuario.get(indice);
                    this.gerenciarDetalhesCurso(selecionado);
                } else {
                    this.visao.mostrarMensagem("Curso inválido.");
                }
            }
        } while (!opcao.equals("R"));
        if (DEBUG) System.out.println("[DEBUG] Saindo do menu Meus Cursos.");
    }

    // ------------------------------------------------------------------------------
    // CRIAÇÃO DE CURSO
    // ------------------------------------------------------------------------------

    // Realiza o fluxo de cadastro de um novo curso para o usuário logado.
    private void incluirCurso(int idUsuario) {
        if (DEBUG) System.out.println("[DEBUG] Iniciando inclusão de curso para usuário ID: " + idUsuario);
        try {
            // Gera código compartilhável de 10 caracteres (estilo NanoID)
            String codigo = this.gerarNanoId();
            // Lê os dados do curso via visão
            Curso novoCurso = this.visao.lerNovoCurso(idUsuario, codigo);
            // Persiste no arquivo e índices
            this.arqCurso.create(novoCurso);
            this.visao.mostrarMensagem("Curso criado com sucesso! Código gerado: " + codigo);
            if (DEBUG) System.out.println("[DEBUG] Curso criado com código: " + codigo);
        } catch (Exception e) {
            this.visao.mostrarMensagem("Erro ao criar curso: " + e.getMessage());
            if (DEBUG) System.out.println("[DEBUG] Exceção ao criar curso: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------------------
    // DETALHES E AÇÕES SOBRE UM CURSO
    // ------------------------------------------------------------------------------

    // Exibe os detalhes de um curso e oferece opções de gerenciamento.
    private void gerenciarDetalhesCurso(Curso curso) {
        if (DEBUG) System.out.println("[DEBUG] Gerenciando detalhes do curso ID: " + curso.getID());
        String opcao;
        do {
            this.visao.mostrarCursoDetalhado(curso);
            opcao = this.visao.menuDetalhesCurso();

            try {
                switch (opcao) {
                    case "A":
                        // Gerenciar inscritos - adiado para o TP2
                        this.visao.mostrarMensagem("Funcionalidade disponível apenas no TP2.");
                        break;
                    case "B":
                        // Corrigir dados do curso
                        this.alterarCurso(curso);
                        break;
                    case "C":
                        // Encerrar inscrições (estado 0 -> 1)
                        if (curso.getEstado() == 0) {
                            curso.setEstado((byte) 1);
                            this.arqCurso.update(curso);
                            this.visao.mostrarMensagem("Inscrições encerradas com sucesso.");
                            if (DEBUG) System.out.println("[DEBUG] Estado do curso alterado para ENCERRADO.");
                        } else {
                            this.visao.mostrarMensagem("Ação inválida para o estado atual.");
                        }
                        break;
                    case "D":
                        // Concluir curso (estado 0 ou 1 -> 2)
                        if (curso.getEstado() == 0 || curso.getEstado() == 1) {
                            curso.setEstado((byte) 2);
                            this.arqCurso.update(curso);
                            this.visao.mostrarMensagem("Curso marcado como concluído.");
                            if (DEBUG) System.out.println("[DEBUG] Estado do curso alterado para CONCLUÍDO.");
                        } else {
                            this.visao.mostrarMensagem("Ação inválida para o estado atual.");
                        }
                        break;
                    case "E":
                        // Cancelar/Excluir curso
                        if (this.visao.confirmar("Deseja realmente cancelar e excluir este curso?")) {
                            this.arqCurso.delete(curso.getID());
                            this.visao.mostrarMensagem("Curso excluído com sucesso.");
                            if (DEBUG) System.out.println("[DEBUG] Curso ID " + curso.getID() + " excluído.");
                            opcao = "R"; // Força saída do loop
                        }
                        break;
                    case "R":
                        // Retornar
                        break;
                    default:
                        this.visao.mostrarMensagem("Opção inválida.");
                }
            } catch (Exception e) {
                this.visao.mostrarMensagem("Erro ao atualizar o curso: " + e.getMessage());
                if (DEBUG) System.out.println("[DEBUG] Exceção na ação " + opcao + ": " + e.getMessage());
            }
        } while (!opcao.equals("R"));
    }

    // Realiza a alteração dos dados básicos de um curso (nome, descrição, data).
    private void alterarCurso(Curso curso) {
        if (DEBUG) System.out.println("[DEBUG] Iniciando alteração do curso ID: " + curso.getID());
        // Obtém os novos dados da visão (campos em branco mantêm os atuais)
        Curso cursoAtualizado = this.visao.lerAlteracoes(curso);
        try {
            if (this.arqCurso.update(cursoAtualizado)) {
                this.visao.mostrarMensagem("Curso atualizado com sucesso.");
                // Atualiza o objeto local para refletir as mudanças na tela
                curso.setNome(cursoAtualizado.getNome());
                curso.setDescricao(cursoAtualizado.getDescricao());
                curso.setDataInicio(cursoAtualizado.getDataInicio());
                if (DEBUG) System.out.println("[DEBUG] Curso atualizado com sucesso.");
            } else {
                this.visao.mostrarMensagem("Falha ao atualizar o curso.");
            }
        } catch (Exception e) {
            this.visao.mostrarMensagem("Erro ao atualizar: " + e.getMessage());
            if (DEBUG) System.out.println("[DEBUG] Exceção no update: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------------------
    // UTILITÁRIOS
    // ------------------------------------------------------------------------------

    // Gera uma string aleatória de 10 caracteres (letras e números) para o código compartilhável.
    private String gerarNanoId() {
        String alfabeto = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int indice = (int) (Math.random() * alfabeto.length());
            sb.append(alfabeto.charAt(indice));
        }
        if (DEBUG) System.out.println("[DEBUG] Código gerado: " + sb.toString());
        return sb.toString();
    }

    // ------------------------------------------------------------------------------
    // FECHAMENTO
    // ------------------------------------------------------------------------------

    // Fecha o arquivo de cursos (deve ser chamado ao encerrar o programa)
    public void close() throws Exception {
        if (DEBUG) System.out.println("[DEBUG] Fechando ControleCurso.");
        this.arqCurso.close();
    }
}