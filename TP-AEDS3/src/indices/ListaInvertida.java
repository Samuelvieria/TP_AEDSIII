package indices;

import aed3.ArvoreBMais;
import aed3.HashExtensivel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe que implementa um Índice Invertido usando Árvore B+ e estruturas de armazenamento.
 * Gerencia a adição, remoção e busca de termos associados a cursos com suas frequências (TF).
 * 
 * Estrutura:
 * - A árvore B+ indexa os termos e seus endereços
 * - Para cada termo, há um arquivo com os pares (idCurso, TF)
 */
public class ListaInvertida {
    
    private ArvoreBMais<ParTermoEndereco> arvore;
    private RandomAccessFile arquivoDados;
    private int totalCursos;
    private final String CAMINHO_INDICE = "./dados/indiceInvertido";
    private final String ARQUIVO_INDICE = "./dados/indiceInvertido/indice.db";
    private final String ARQUIVO_DADOS = "./dados/indiceInvertido/dados.db";
    private final String ARQUIVO_METADATA = "./dados/indiceInvertido/metadata.db";

    /**
     * Construtor que inicializa o índice invertido
     */
    public ListaInvertida() throws Exception {
        // Criar diretório se não existir
        File d = new File(CAMINHO_INDICE);
        if (!d.exists())
            d.mkdirs();

        // Inicializar a árvore B+
        arvore = new ArvoreBMais<>(
            ParTermoEndereco.class.getConstructor(),
            4,
            ARQUIVO_INDICE
        );

        // Inicializar arquivo de dados
        arquivoDados = new RandomAccessFile(ARQUIVO_DADOS, "rw");
        if (arquivoDados.length() < 4) {
            arquivoDados.seek(0);
            arquivoDados.writeInt(0);  // Próximo endereço disponível
        }

        // Carregar total de cursos da metadata
        carregarMetadata();
    }

    /**
     * Carrega o total de cursos do arquivo de metadata
     */
    private void carregarMetadata() throws Exception {
        RandomAccessFile metadata = new RandomAccessFile(ARQUIVO_METADATA, "rw");
        if (metadata.length() < 4) {
            metadata.seek(0);
            metadata.writeInt(0);  // Inicialmente 0 cursos
            this.totalCursos = 0;
        } else {
            metadata.seek(0);
            this.totalCursos = metadata.readInt();
        }
        metadata.close();
    }

    /**
     * Salva o total de cursos na metadata
     */
    private void salvarMetadata() throws Exception {
        RandomAccessFile metadata = new RandomAccessFile(ARQUIVO_METADATA, "rw");
        metadata.seek(0);
        metadata.writeInt(this.totalCursos);
        metadata.close();
    }

    /**
     * Adiciona um termo ao índice com seu TF
     * Se o termo já existe para o curso, atualiza o TF
     * Se o termo não existe, cria uma nova entrada
     */
    public void adicionarTermo(String termo, int idCurso, float tf) throws Exception {
        if (termo == null || termo.trim().isEmpty())
            return;

        termo = termo.toLowerCase().trim();

        // Buscar o termo na árvore
        ParTermoEndereco par = new ParTermoEndereco(termo, -1);
        long[] resultados = arvore.read(par);

        long endereco;
        if (resultados != null && resultados.length > 0) {
            // Termo já existe
            endereco = resultados[0];
            atualizarOuAdicionarCursoNoTermo(endereco, idCurso, tf);
        } else {
            // Termo novo - criar entrada
            endereco = criarNovoTermo(termo);
            par.setEndereco(endereco);
            arvore.create(par);
            adicionarCursoAoTermo(endereco, idCurso, tf);
        }
    }

    /**
     * Cria um novo termo no arquivo de dados
     */
    private long criarNovoTermo(String termo) throws Exception {
        arquivoDados.seek(0);
        int proximoEndereco = arquivoDados.readInt();
        
        long endereco = proximoEndereco;
        arquivoDados.seek(endereco);
        arquivoDados.writeInt(0);  // Quantidade de cursos inicial = 0
        
        // Atualizar próximo endereço disponível
        int novoProximo = (int) (endereco + 4 + (100 * 8));  // 4 bytes para quantidade + espaço para até 100 cursos
        arquivoDados.seek(0);
        arquivoDados.writeInt(novoProximo);

        return endereco;
    }

    /**
     * Adiciona um novo curso a um termo existente
     */
    private void adicionarCursoAoTermo(long endereco, int idCurso, float tf) throws Exception {
        arquivoDados.seek(endereco);
        int quantidade = arquivoDados.readInt();

        // Verificar se o curso já existe
        for (int i = 0; i < quantidade; i++) {
            int id = arquivoDados.readInt();
            if (id == idCurso) {
                // Curso já existe, atualizar TF (sobrescrever float que acabou de ser lido)
                arquivoDados.writeFloat(tf);
                return;
            }
            arquivoDados.readFloat();  // Pular TF
        }

        // Adicionar novo curso
        if (quantidade < 100) {  // Limite de 100 cursos por termo
            arquivoDados.seek(endereco + 4 + (quantidade * 8));
            arquivoDados.writeInt(idCurso);
            arquivoDados.writeFloat(tf);
            
            // Atualizar quantidade
            arquivoDados.seek(endereco);
            arquivoDados.writeInt(quantidade + 1);
        }
    }

    /**
     * Atualiza ou adiciona um curso a um termo
     */
    private void atualizarOuAdicionarCursoNoTermo(long endereco, int idCurso, float tf) throws Exception {
        arquivoDados.seek(endereco);
        int quantidade = arquivoDados.readInt();

        // Procurar o curso
        for (int i = 0; i < quantidade; i++) {
            long posInicial = arquivoDados.getFilePointer();
            int id = arquivoDados.readInt();
            if (id == idCurso) {
                // Atualizar TF
                arquivoDados.writeFloat(tf);
                return;
            }
            arquivoDados.readFloat();  // Pular TF
        }

        // Curso não encontrado, adicionar novo
        if (quantidade < 100) {
            arquivoDados.seek(endereco + 4 + (quantidade * 8));
            arquivoDados.writeInt(idCurso);
            arquivoDados.writeFloat(tf);
            
            // Atualizar quantidade
            arquivoDados.seek(endereco);
            arquivoDados.writeInt(quantidade + 1);
        }
    }

    /**
     * Remove um termo específico (todos os cursos associados)
     */
    public void removerTermo(String termo) throws Exception {
        if (termo == null || termo.trim().isEmpty())
            return;

        termo = termo.toLowerCase().trim();
        ParTermoEndereco par = new ParTermoEndereco(termo, -1);
        arvore.delete(par);
    }

    /**
     * Remove um curso específico de um termo
     */
    public void removerTermo(String termo, int idCurso) throws Exception {
        if (termo == null || termo.trim().isEmpty())
            return;

        termo = termo.toLowerCase().trim();

        // Buscar o termo
        ParTermoEndereco par = new ParTermoEndereco(termo, -1);
        long[] resultados = arvore.read(par);

        if (resultados == null || resultados.length == 0)
            return;

        long endereco = resultados[0];
        arquivoDados.seek(endereco);
        int quantidade = arquivoDados.readInt();

        // Procurar e remover o curso
        List<Integer> ids = new ArrayList<>();
        List<Float> tfs = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            int id = arquivoDados.readInt();
            float tf = arquivoDados.readFloat();
            if (id != idCurso) {
                ids.add(id);
                tfs.add(tf);
            }
        }

        // Reescrever os dados
        arquivoDados.seek(endereco);
        arquivoDados.writeInt(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            arquivoDados.writeInt(ids.get(i));
            arquivoDados.writeFloat(tfs.get(i));
        }

        // Se não há mais cursos, remover o termo
        if (ids.isEmpty()) {
            removerTermo(termo);
        }
    }

    /**
     * Busca um termo e retorna a lista de pares (idCurso, TF)
     */
    public List<ParCursoTF> buscarTermo(String termo) throws Exception {
        List<ParCursoTF> resultado = new ArrayList<>();

        if (termo == null || termo.trim().isEmpty())
            return resultado;

        termo = termo.toLowerCase().trim();

        // Buscar na árvore
        ParTermoEndereco par = new ParTermoEndereco(termo, -1);
        long[] enderecos = arvore.read(par);

        if (enderecos == null || enderecos.length == 0)
            return resultado;

        long endereco = enderecos[0];

        // Ler os pares (idCurso, TF) do arquivo
        arquivoDados.seek(endereco);
        int quantidade = arquivoDados.readInt();

        for (int i = 0; i < quantidade; i++) {
            int idCurso = arquivoDados.readInt();
            float tf = arquivoDados.readFloat();
            resultado.add(new ParCursoTF(idCurso, tf));
        }

        return resultado;
    }

    /**
     * Retorna todos os termos indexados
     */
    public List<ParTermoEndereco> obterTodosTermos() throws Exception {
        // Nota: A árvore B+ não fornece um método direto para iterar todos os elementos
        // Esta é uma limitação da implementação atual
        List<ParTermoEndereco> resultado = new ArrayList<>();
        // Implementação simplificada - seria necessário adicionar método na ArvoreBMais
        // para navegação completa
        return resultado;
    }

    /**
     * Obtém a quantidade de cursos cadastrados
     */
    public int obterTotalCursos() {
        return totalCursos;
    }

    /**
     * Define o total de cursos (usado quando um novo curso é adicionado)
     */
    public void atualizarTotalCursos(int total) throws Exception {
        this.totalCursos = total;
        salvarMetadata();
    }

    /**
     * Obtém a quantidade de cursos que contêm um termo específico
     */
    public int obterQuantidadeCursosComTermo(String termo) throws Exception {
        if (termo == null || termo.trim().isEmpty())
            return 0;

        termo = termo.toLowerCase().trim();

        ParTermoEndereco par = new ParTermoEndereco(termo, -1);
        long[] enderecos = arvore.read(par);

        if (enderecos == null || enderecos.length == 0)
            return 0;

        long endereco = enderecos[0];
        arquivoDados.seek(endereco);
        return arquivoDados.readInt();  // Retorna a quantidade de cursos
    }

    /**
     * Fecha o índice e libera recursos
     */
    public void fechar() throws Exception {
        if (arquivoDados != null) {
            // Sincronizar dados com disco antes de fechar
            if (arquivoDados.getChannel() != null) {
                arquivoDados.getChannel().force(true);
            }
            arquivoDados.close();
        }
    }
}
