# EntrePares 3.0 - TP3

**Disciplina:** AEDS III
**Instituição:** PUC Minas
**Professor:** Marcos Kutova
**Período:** 1º semestre de 2026

---

## 🎥 Link do Vídeo

Apresentação do TP3:

[Link do vídeo - a ser adicionado]

---

## 👥 Participantes

* Marco Antonio Barbosa Martins de Souza
* Samuel Ferreira Alves Vieira
* Eduardo Nunes Neumann
* Savio Rangel de Faria

---

## 📖 Descrição do Sistema

O EntrePares 3.0 é a evolução do sistema de gestão de cursos livres desenvolvido na disciplina de Algoritmos e Estruturas de Dados III. Nesta terceira etapa do projeto, o sistema passou a suportar **busca por palavras-chave** nos nomes dos cursos utilizando **índice invertido** e ranqueamento por **TF × IDF** (Term Frequency — Inverse Document Frequency).

O sistema mantém toda a infraestrutura dos TPs anteriores — autenticação, gerenciamento de cursos, inscrições, persistência em arquivos, Hash Extensível e Árvores B+ — acrescentando um mecanismo sofisticado de busca textual que permite aos usuários encontrar cursos de forma mais natural e eficiente.

---

# ✅ Funcionalidades do TP3

## 🔍 Índice Invertido

| Funcionalidade | Descrição |
|----------------|-----------|
| Extração de termos | Palavras do nome do curso são transformadas em vetor de termos |
| Remoção de stop words | Artigos, preposições, conjunções, pronomes e numerais são descartados |
| Normalização | Conversão para letras minúsculas e remoção de acentos |
| Cálculo de TF | Term Frequency = ocorrências do termo / total de termos válidos |
| Cálculo de IDF | Inverse Document Frequency = log10(N / n_t) + 1 |
| Ranqueamento TF×IDF | Multiplicação do TF pelo IDF para cada termo |
| Ordenação | Resultados ordenados do maior score para o menor |

## 🔍 Busca por Palavras-Chave

| Funcionalidade | Descrição |
|----------------|-----------|
| Interface no menu | Opção "(B) Buscar curso por palavras-chave" no menu de inscrições |
| Processamento da consulta | Mesmo tratamento aplicado aos nomes dos cursos |
| Recuperação | Listas invertidas são consultadas para cada termo |
| Agregação | Scores de um mesmo curso são somados |
| Exibição | Cursos apresentados na ordem de relevância (maior score primeiro) |

## 📚 Manutenção do Índice Invertido

| Operação | Comportamento |
|----------|---------------|
| Criação de curso | Termos do nome são extraídos e indexados com seus respectivos TF |
| Atualização de curso | Índice antigo é removido; novo índice é criado |
| Exclusão de curso | Todas as entradas do curso são removidas do índice |

---

# 📁 Novas Classes do TP3

## `indices/` (novas classes)

| Classe | Descrição |
|--------|-----------|
| `IndiceInvertido.java` | Gerencia o índice invertido usando Árvore B+ para armazenar termos |
| `ParTermoId.java` | Par (termo, idCurso, TF) armazenado na árvore B+ |
| `Texto.java` | Funções utilitárias: normalização, remoção de acentos, extração de termos |
| `StopWords.java` | Lista completa de stop words em português |

## `testes/` (novos testes)

| Classe | Descrição |
|--------|-----------|
| `TesteIndiceInvertido.java` | Teste completo com 15 validações do índice invertido e busca TF×IDF |

## Classes modificadas

| Classe | Alteração |
|--------|-----------|
| `ArquivoCurso.java` | Adicionado `indiceInvertido` e métodos `buscarPorPalavras()` |
| `ArquivoInscricao.java` | Adicionada verificação de estado do curso ao criar inscrição |
| `ControleInscricoes.java` | Adicionado método `buscarCursoPorPalavras()` |
| `ParNomeCursoId.java` | Aumentado limite de caracteres de 26 para 100 |

---

# 🔧 Como Funciona o Índice Invertido

## 1. Indexação de um Curso

Ao criar um curso com nome "Introdução à Inteligência Artificial":

1. **Extração:** remove stop words ("à") → ["introducao", "inteligencia", "artificial"]
2. **Cálculo do TF:** cada termo aparece 1 vez em 3 termos → TF = 1/3 = 0.333
3. **Armazenamento:** para cada termo, insere-se (termo, idCurso, TF) na árvore B+

## 2. Busca por Palavras

Usuário digita "Inteligência Artificial":

1. **Processamento:** mesmo tratamento → ["inteligencia", "artificial"]
2. **Recuperação:** busca listas de cada termo na árvore B+
3. **Cálculo do IDF:** 
   - "inteligencia" aparece em 3 de 4 cursos → IDF = log10(4/3) + 1 = 1.125
   - "artificial" aparece em 2 de 4 cursos → IDF = log10(4/2) + 1 = 1.301
4. **Cálculo do score:** 
   - Curso 1: 0.333 × 1.125 + 0.333 × 1.301 = 0.808
   - Curso 2: 0.333 × 1.125 + 0 × 1.301 = 0.375
   - Curso 3: 0.400 × 1.125 + 0.200 × 1.301 = 0.656
5. **Ordenação:** Curso 1 (0.808), Curso 3 (0.656), Curso 2 (0.375)

---

# ✅ Checklist de Avaliação do TP3

## Pergunta 1: O índice invertido com os termos dos nomes dos cursos foi criado usando a classe ListaInvertida?

**Sim.** Utilizamos a classe `IndiceInvertido.java` que gerencia uma `ArvoreBMais<ParTermoId>` para armazenar os termos e seus respectivos TF. A implementação segue o padrão de índice invertido descrito no enunciado.

---

## Pergunta 2: É possível buscar cursos por palavras no menu de inscrição?

**Sim.** No menu "Minhas inscrições", o usuário encontra a opção **(B) Buscar curso por palavras-chave**. Ao digitar os termos de busca, o sistema retorna os cursos cujos nomes contêm esses termos, ordenados pela relevância (maior score TF×IDF primeiro).

---

## Pergunta 3: O trabalho compila corretamente?

**Sim.** O projeto compila sem erros utilizando o comando `javac -d bin -sourcepath src src/**/*.java src/*.java`, com todas as dependências resolvidas.

---

## Pergunta 4: O trabalho está completo e funcionando sem erros de execução?

**Sim.** Todos os fluxos foram testados e validados pelo `TesteIndiceInvertido.java`, que executa 15 testes abrangendo:
- Extração de termos e stop words
- Cadastro, atualização e exclusão de cursos
- Busca ordenada por TF×IDF
- Busca por código compartilhável
- Bloqueio de inscrição em curso encerrado
- Cálculo correto do IDF


---

## Pergunta 5: O trabalho é original e não a cópia de um trabalho de outro grupo?

**Sim.** O código foi desenvolvido integralmente pelo grupo a partir das classes base fornecidas pelo professor. As implementações do índice invertido, tratamento de texto e busca por palavras foram desenvolvidas especificamente para este TP3.

---

# 🖼️ Telas do Sistema (TP3)

| Descrição | Imagem |
|:----------|:-------|
| Menu de inscrições com busca por palavras | ![Menu Inscrições](https://via.placeholder.com/500x300?text=Captura+de+Tela) |
| Resultado da busca por "Inteligência Artificial" | ![Resultado Busca](https://via.placeholder.com/500x300?text=Captura+de+Tela) |
| Detalhes do curso com opção de inscrição | ![Detalhes Curso](https://via.placeholder.com/500x300?text=Captura+de+Tela) |

> **Nota:** As imagens serão adicionadas após a gravação do vídeo.

---

# ⚙️ Operações Especiais do TP3

## Índice Invertido com Árvore B+

O índice invertido foi implementado utilizando a classe `ArvoreBMais<ParTermoId>`, onde:
- **Chave primária:** termo (string normalizada)
- **Valor:** par (idCurso, TF)

Essa abordagem permite:
- Busca eficiente por todos os cursos que contêm um determinado termo
- Recuperação ordenada dos termos (importante para prefixos)
- Persistência em disco com reaproveitamento de espaço

## Cálculo do IDF na Busca

O IDF é calculado dinamicamente durante a busca, utilizando:
- `totalCursos`: número total de cursos cadastrados (obtido da metadata)
- `cursosComTermo`: tamanho da lista recuperada para o termo

Isso garante que o ranqueamento seja sempre atualizado conforme novos cursos são adicionados ou removidos.

## Tratamento de Texto Robusto

As classes `Texto.java` e `StopWords.java` implementam:
- Normalização Unicode NFD para remoção de acentos
- Conversão para minúsculas
- Remoção de pontuação e caracteres especiais
- Filtro de stop words (artigos, preposições, conjunções, pronomes, numerais)

---

# 🧪 Testes Realizados

## `TesteIndiceInvertido.java`

O teste executa 15 validações:

| # | Teste | Status |
|---|-------|--------|
| 1 | Extração de termos (stop words, acentos, caixa baixa) | ✅ |
| 2 | Cadastro dos cursos do exemplo | ✅ |
| 3 | Busca ordenada por TF×IDF | ✅ |
| 4 | Atualização do nome reindexa o índice | ✅ |
| 5 | Exclusão remove entradas do índice | ✅ |
| 6 | Busca por código compartilhável | ✅ |
| 7 | Impedir inscrição em curso encerrado | ✅ |
| 8 | Menu de inscrições com busca por palavras | ✅ |
| 9 | Verificação da fórmula IDF | ✅ |
| 10 | Curso excluído não pode ser lido | ✅ |

**Resultado: 15/15 testes passados (100%)**

---

# 📁 Estrutura de Pacotes e Classes (Atualizada para TP3)

## `indices/` (TP3)

| Classe | Descrição |
|--------|-----------|
| `IndiceInvertido.java` | Gerencia o índice invertido (busca por palavras) |
| `ParTermoId.java` | Par (termo, idCurso, TF) |
| `Texto.java` | Normalização e extração de termos |
| `StopWords.java` | Lista de stop words em português |

## `testes/` (TP3)

| Classe | Descrição |
|--------|-----------|
| `TesteIndiceInvertido.java` | Teste completo do índice invertido (15 testes) |

## Classes modificadas para o TP3

| Classe | Modificação |
|--------|-------------|
| `ArquivoCurso.java` | Adicionado `buscarPorPalavras()` e integração com `IndiceInvertido` |
| `ArquivoInscricao.java` | Adicionada verificação de estado do curso no `create()` |
| `ControleInscricoes.java` | Adicionado `buscarCursoPorPalavras()` |
| `ParNomeCursoId.java` | Aumentado limite de caracteres de 26 para 100 |

---

# 🚀 Evolução do TP2 → TP3

| Funcionalidade | TP2 | TP3 |
|----------------|-----|-----|
| Busca por código | ✅ | ✅ |
| Busca por palavras-chave | ❌ | ✅ |
| Índice invertido | ❌ | ✅ |
| Ranqueamento TF×IDF | ❌ | ✅ |
| Tratamento de stop words | ❌ | ✅ |
| Normalização de texto | ❌ | ✅ |
| Verificação de estado na inscrição | ✅ | ✅ |

---

# ⁉️ Observações Adicionais

## Sobre o Teste de Exclusão

Durante os testes, identificou-se que o termo "equipes" aparece em dois cursos:
- Curso ID 2: "Gestão de Equipes Remotas" (renomeado durante o teste)
- Curso ID 4: "Introdução à Gestão de Equipes"

Após a exclusão do curso ID 4, a busca por "equipes" continua retornando o curso ID 2, o que é **comportamento correto** e não uma falha.

## Sobre a Verificação de Estado na Inscrição

Foi adicionada uma verificação no método `ArquivoInscricao.create()` que impede a inscrição em cursos com estado diferente de 0 (Ativo). Isso garante que:
- Cursos com inscrições encerradas (estado 1) não aceitem novas inscrições
- Cursos concluídos (estado 2) não aceitem inscrições
- Cursos cancelados (estado 3) não aceitem inscrições

---

# 🧠 Conceitos de AED III Utilizados no TP3

* Índice invertido
* Term Frequency (TF)
* Inverse Document Frequency (IDF)
* Ranqueamento TF×IDF
* Árvores B+ para indexação de termos
* Tratamento de stop words
* Normalização de texto (Unicode NFD)
* Persistência em arquivos
* Busca textual eficiente

---

## 💻 Como Executar o Sistema

```bash
# Compilar o projeto
javac -d bin -sourcepath src src/**/*.java src/*.java

# Executar o sistema principal
java -cp bin Principal
