# EntrePares 1.0 - TP1

**Disciplina:** AEDS III  
**Instituição:** PUC Minas  
**Professor:** Marcos Kutova  
**Período:** 1º semestre de 2026

---

## 👥 Participantes

- Marco Antonio Barbosa Martins de Souza
- Samuel Ferreira Alves Vieira
- Eduardo Nunes Neumann
- Savio Rangel de Faria

---

## 📖 Descrição do Sistema

O EntrePares 1.0 é um sistema de gestão de cursos livres desenvolvido como primeiro trabalho prático da disciplina de Algoritmos e Estruturas de Dados III. 
A aplicação permite que usuários se cadastrem, façam login e gerenciem seus próprios cursos, estabelecendo um relacionamento 1:N entre usuário e cursos.

### Funcionalidades Implementadas

- Autenticação de Usuários
  - Cadastro de novo usuário com validação de email único.
  - Login por email e senha (hash armazenado).
  - Recuperação de senha via pergunta/resposta secreta.

- Gerenciamento de Usuários (Meus Dados)
  - Visualização dos dados do perfil.
  - Alteração de nome, email e senha.
  - Exclusão da própria conta.

- Gerenciamento de Cursos (Meus Cursos)
  - Criação de cursos com geração automática de código compartilhável (NanoID 10 caracteres).
  - Listagem ordenada alfabeticamente dos cursos do usuário logado.
  - Visualização detalhada de cada curso.
  - Edição dos dados do curso (nome, descrição, data de início).
  - Alteração de estado do curso:
    - Encerrar inscrições (0 → 1)
    - Concluir curso (0/1 → 2)
    - Cancelar / Excluir curso (remoção lógica)
  - Isolamento de dados: cada usuário vê apenas seus próprios cursos.

- Persistência e Índices
  - Armazenamento em arquivos com registros de tamanho variável e reaproveitamento de espaço.
  - Índice direto baseado em **Hash Extensível** (ID → endereço).
  - Índice de email com **Hash Extensível** (busca exata).
  - Índice de código de curso com **Hash Extensível**.
  - Índice de nome de curso com **Árvore B+** (permite duplicatas e busca ordenada).
  - Índice de relacionamento 1:N (usuário → cursos) com **Árvore B+**.

---

## 📁 Estrutura de Pacotes e Classes Criadas

src/
├── aed3/ (classes genéricas fornecidas)
│ ├── Arquivo.java
│ ├── HashExtensivel.java
│ ├── ArvoreBMais.java
│ ├── InterfaceEntidade.java
│ ├── InterfaceHashExtensivel.java
│ ├── InterfaceArvoreBMais.java
│ └── ParIDEndereco.java
│
├── entidades/
│ ├── Usuario.java (entidade usuário)
│ └── Curso.java (entidade curso)
│
├── indices/
│ ├── ParEmailId.java (par email → idUsuario)
│ ├── ParCodigoId.java (par código → idCurso)
│ ├── ParNomeCursoId.java (par nome → idCurso)
│ └── ParUsuarioCursoId.java (par idUsuario → idCurso)
│
├── arquivos/
│ ├── ArquivoUsuario.java (CRUD de usuários + índices)
│ └── ArquivoCurso.java (CRUD de cursos + índices)
│
├── visao/
│ ├── VisaoUsuario.java (interface textual para usuário)
│ └── VisaoCurso.java (interface textual para cursos)
│
├── controle/
│ ├── Sessao.java (gerência do usuário logado)
│ ├── ControleUsuario.java (lógica de autenticação e perfil)
│ └── ControleCurso.java (lógica de gestão de cursos)
│
├── testes/
│ └── PopularBD.java (popula base com dados de exemplo)
│
└── Principal.java (classe principal com menu)

---

## 🖼️ Telas do Sistema 

---

## ✅ Checklist de Avaliação

## Pergunta 1: Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
Sim. A classe ArquivoUsuario estende Arquivo<Usuario> (que internamente utiliza HashExtensivel<ParIDEndereco> para índice direto ID → endereço) e acrescenta 
HashExtensivel<ParEmailId> para busca por email. Os métodos create, read, update, delete, login e recuperarSenha foram implementados e testados, comprovando o funcionamento 
completo do CRUD com os índices exigidos.

## Pergunta 2: Há um CRUD de cursos (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
Sim. A classe ArquivoCurso estende Arquivo<Curso> e adiciona três índices: HashExtensivel<ParCodigoId> (busca por código compartilhável), ArvoreBMais<ParNomeCursoId> 
(busca ordenada por nome) e ArvoreBMais<ParUsuarioCursoId> (relacionamento 1:N). Os métodos create, read, update, delete, listarPorUsuario e readNome operam corretamente, 
atendendo a todos os requisitos de CRUD e indexação.

## Pergunta 3: Os cursos estão vinculados aos usuários usando o idUsuario como chave estrangeira?
Sim. A entidade Curso contém o atributo idUsuario, que é preenchido com o ID do usuário logado (Sessao.getIdUsuarioLogado()) durante a criação do curso. O índice 
ParUsuarioCursoId armazena esse vínculo na árvore B+, garantindo a integridade referencial.

## Pergunta 4: Há uma árvore B+ que registre o relacionamento 1:N entre usuários e cursos?
Sim. O índice indiceUsuario em ArquivoCurso é uma ArvoreBMais<ParUsuarioCursoId>, onde a chave é o idUsuario. O método listarPorUsuario(int idUsuario) percorre essa árvore
e retorna todos os cursos associados ao usuário, demonstrando claramente o relacionamento 1:N.

## Pergunta 5: Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade)?
Sim. A classe ArquivoUsuario estende a classe genérica Arquivo<Usuario> (que internamente utiliza uma Tabela Hash Extensível para o índice direto ParIDEndereco). 
Além do índice direto obrigatório, ArquivoUsuario acrescenta um índice adicional baseado em Hash Extensível – ParEmailId – para busca rápida e validação de unicidade de 
emails. Os métodos create, read, update, delete, bem como as operações específicas login e recuperarSenha, foram implementados e testados, garantindo o pleno funcionamento
do CRUD de usuários com os índices exigidos.

## Pergunta 6: O trabalho compila corretamente?
Sim. O projeto compila sem erros utilizando o comando javac -d bin -sourcepath src src/**/*.java src/*.java, com todas as dependências resolvidas e pacotes organizados 
conforme a estrutura definida.

## Pergunta 7: O trabalho está completo e funcionando sem erros de execução?
Sim. Todos os fluxos principais (autenticação, CRUD de usuário, CRUD de curso com vínculo, navegação entre menus, transições de estado dos cursos e persistência em arquivo) 
foram testados e executam conforme as especificações, sem erros em tempo de execução.

## Pergunta 8: O trabalho é original e não a cópia de um trabalho de outro grupo?
Sim. O código foi desenvolvido pelo próprio grupo a partir das classes base fornecidas pelo professor (Arquivo, HashExtensivel, ArvoreBMais), adaptando e estendendo para as 
entidades Usuario e Curso com índices personalizados (ParEmailId, ParCodigoId, ParNomeCursoId, ParUsuarioCursoId). As classes de visão e controle foram implementadas 
seguindo o padrão MVC solicitado, sem reaproveitamento de código de outros grupos.

## ⁉️ Observações Adicionais
A classe testes.PopularBD foi desenvolvida exclusivamente para agilizar a validação do sistema. Seu propósito é povoar o banco de dados com usuários e cursos de exemplo, 
dispensando a necessidade de cadastros manuais repetitivos durante os testes.

## Funcionamento:
Cria três usuários (joao@email.com, maria@email.com, carlos@email.com) com senhas padronizadas.
Gera cinco cursos automaticamente, cada um vinculado ao respectivo usuário.
Utiliza os mesmos métodos create() das classes ArquivoUsuario e ArquivoCurso, garantindo que todos os índices (Hash Extensível e Árvores B+) sejam corretamente atualizados.
A opção (P) Popular BD (teste) está disponível no menu principal (após o login) e pode ser removida ou ocultada na versão final por meio da constante DEBUG.

## Exibição dos IDs ao Usuário
Durante as operações de cadastro, o sistema exibe intencionalmente o ID sequencial gerado para cada entidade:
"Usuário cadastrado com sucesso! Seu ID é X."
(Para cursos, o ID interno não é exibido, apenas o código compartilhável.)

## Objetivo dessa exibição:
Demonstrar de forma transparente ao professor/avaliador que as inserções estão sendo realizadas de maneira sequencial e conforme as regras de geração de identificadores 
únicos estabelecidas no enunciado do trabalho. Em um sistema em produção, essa informação provavelmente seria omitida, mas para fins de correção acadêmica ela evidencia o 
correto funcionamento do mecanismo de chaves primárias.
