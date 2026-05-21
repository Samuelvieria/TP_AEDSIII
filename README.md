# EntrePares 2.0 - TP2

**Disciplina:** AEDS III
**Instituição:** PUC Minas
**Professor:** Marcos Kutova
**Período:** 1º semestre de 2026

---

## 🎥 Link do Vídeo

apresentação do TP2:

`https://SEU-LINK-AQUI`

---

## 👥 Participantes

* Marco Antonio Barbosa Martins de Souza
* Samuel Ferreira Alves Vieira
* Eduardo Nunes Neumann
* Savio Rangel de Faria

---

## 📖 Descrição do Sistema

O EntrePares 2.0 é a evolução do sistema de gestão de cursos livres desenvolvido na disciplina de Algoritmos e Estruturas de Dados III. Nesta segunda etapa do projeto, o sistema passou a suportar o relacionamento N:N entre usuários e cursos por meio da entidade `Inscricao`, permitindo que usuários se inscrevam em cursos criados por outros usuários.

O sistema mantém toda a infraestrutura do TP1 — autenticação, gerenciamento de cursos, persistência em arquivos, Hash Extensível e Árvores B+ — acrescentando mecanismos de inscrição, listagem de participantes, controle de estados do curso e exportação de relatórios.

---

# ✅ Funcionalidades Implementadas

## 👤 Usuários

* Cadastro de usuários com validação de email único.
* Login utilizando email e senha.
* Recuperação de senha via pergunta secreta.
* Alteração de dados cadastrais.
* Exclusão da própria conta.
* Verificação de integridade referencial antes da exclusão.

---

## 📚 Cursos

* Criação de cursos com geração automática de NanoID.
* Alteração de dados do curso.
* Alteração de estados:

  * Ativo
  * Inscrições encerradas
  * Concluído
  * Cancelado
* Listagem ordenada de cursos por data.
* Busca de cursos por código compartilhável.
* Controle de integridade referencial.

---

## 📝 Inscrições (Relacionamento N:N)

* Inscrição de usuários em cursos.
* Cancelamento de inscrição.
* Listagem de cursos inscritos pelo usuário.
* Listagem de inscritos em um curso.
* Impedimento de inscrições duplicadas.
* Impedimento de inscrição em cursos:

  * cancelados
  * concluídos
  * com inscrições encerradas

---

## 📊 Relatórios e Exportação

* Visualização dos inscritos em cada curso.
* Exportação da lista de inscritos em formato CSV.
* Relatórios utilizando Árvores B+ para recuperação eficiente.

---

## ⚙️ Persistência e Estruturas de Dados

* Persistência em arquivos de tamanho variável.
* Reaproveitamento de espaços vazios.
* Índice direto com Hash Extensível.
* Índice de email com Hash Extensível.
* Índice de código de curso com Hash Extensível.
* Índice por nome de curso com Árvore B+.
* Índice de relacionamento usuário → cursos com Árvore B+.
* Índice de relacionamento usuário → inscrições com Árvore B+.
* Índice de relacionamento curso → inscrições com Árvore B+.

---

# 📁 Estrutura de Pacotes e Classes

## `aed3/`

Classes genéricas fornecidas:

* `Arquivo.java`
* `HashExtensivel.java`
* `ArvoreBMais.java`
* `InterfaceEntidade.java`
* `InterfaceHashExtensivel.java`
* `InterfaceArvoreBMais.java`
* `ParIDEndereco.java`

---

## `entidades/`

* `Usuario.java`
* `Curso.java`
* `Inscricao.java`

---

## `indices/`

* `ParEmailId.java`
* `ParCodigoId.java`
* `ParNomeCursoId.java`
* `ParUsuarioCursoId.java`
* `ParUsuarioInscricao.java`
* `ParCursoInscricao.java`

---

## `arquivos/`

* `ArquivoUsuario.java`
* `ArquivoCurso.java`
* `ArquivoInscricao.java`

---

## `controle/`

* `ControleUsuario.java`
* `ControleCurso.java`
* `ControleInscricao.java`
* `Sessao.java`

---

## `visao/`

* `VisaoUsuario.java`
* `VisaoCurso.java`
* `VisaoInscricao.java`

---

## `testes/`

* `PopularBD.java`

---

## Classe principal

* `Principal.java`

---

# 🔗 Relacionamento N:N

O relacionamento entre usuários e cursos é implementado pela entidade `Inscricao`, permitindo que:

* um usuário participe de vários cursos;
* um curso possua vários participantes.

A persistência das inscrições é feita pela classe `ArquivoInscricao`, que utiliza duas Árvores B+:

* `ArvoreBMais<ParUsuarioInscricao>`

  * recupera todas as inscrições de um usuário;

* `ArvoreBMais<ParCursoInscricao>`

  * recupera todos os inscritos de um curso.

Essa modelagem garante buscas eficientes e atende aos requisitos do TP2.

---

# ⚙️ Operações Especiais Implementadas

## NanoID para cursos

Cada curso recebe automaticamente um código alfanumérico único de 10 caracteres utilizado como identificador público.

---

## Integridade referencial

O sistema impede:

* exclusão de usuários que possuam cursos ou inscrições;
* exclusão de cursos que possuam inscrições.

Isso evita registros órfãos e mantém a consistência da base de dados.

---

## Controle de estados

As regras de negócio impedem novas inscrições em cursos:

* cancelados;
* concluídos;
* com inscrições encerradas.

---

## Exportação CSV

O sistema permite exportar os inscritos de um curso para um arquivo `.csv`, contendo:

* nome do usuário;
* email;
* identificador do curso.

---

# ✅ Checklist de Avaliação

## Pergunta 1: Há um CRUD de usuários funcionando corretamente?

Sim. A classe `ArquivoUsuario` implementa CRUD completo com Hash Extensível para índice direto e índice de email.

---

## Pergunta 2: Há um CRUD de cursos funcionando corretamente?

Sim. A classe `ArquivoCurso` implementa CRUD completo utilizando Hash Extensível e Árvores B+.

---

## Pergunta 3: Há um CRUD de inscrições funcionando corretamente?

Sim. A classe `ArquivoInscricao` implementa CRUD da entidade de relacionamento N:N entre usuários e cursos.

---

## Pergunta 4: Existe relacionamento N:N implementado com Árvores B+?

Sim. O sistema utiliza:

* `ArvoreBMais<ParUsuarioInscricao>`
* `ArvoreBMais<ParCursoInscricao>`

para indexar as inscrições.

---

## Pergunta 5: O sistema impede inscrições duplicadas?

Sim. Antes da criação de uma inscrição, o sistema verifica se o usuário já está inscrito no curso.

---

## Pergunta 6: Há controle de integridade referencial?

Sim. O sistema impede exclusões que causariam registros órfãos.

---

## Pergunta 7: O sistema exporta relatórios?

Sim. O sistema exporta listas de inscritos em formato CSV.

---

## Pergunta 8: O trabalho compila corretamente?

Sim. O projeto compila corretamente utilizando os comandos especificados pela disciplina.

---

## Pergunta 9: O trabalho está funcionando sem erros de execução?

Sim. Todos os fluxos principais foram testados:

* autenticação;
* CRUD de usuários;
* CRUD de cursos;
* CRUD de inscrições;
* exportação CSV;
* navegação;
* persistência.

---

## Pergunta 10: O trabalho é original?

Sim. O projeto foi desenvolvido integralmente pelo grupo utilizando apenas as classes-base fornecidas pelo professor.

---

# ⁉️ Observações Adicionais

A classe `PopularBD.java` continua sendo utilizada para facilitar os testes do sistema, agora incluindo também a geração automática de inscrições.

Ela:

* cria usuários;
* cria cursos;
* cria inscrições automaticamente;
* atualiza todos os índices necessários.

---

# 🧠 Conceitos de AED III Utilizados

* Hash Extensível
* Árvores B+
* Persistência em arquivos
* CRUD indexado
* Relacionamento 1:N
* Relacionamento N:N
* Integridade referencial
* Reaproveitamento de espaço
* Registros de tamanho variável
* Índices indiretos
* Exportação de relatórios
* Estruturas de dados externas

---

# 🚀 Evolução do TP1 → TP2

A principal evolução do sistema nesta etapa foi a transformação do modelo de cursos isolados em uma plataforma colaborativa de inscrições, utilizando relacionamento N:N e estruturas indexadas eficientes para recuperação de dados em larga escala.

O TP2 amplia significativamente a complexidade estrutural do sistema, consolidando o uso de Árvores B+ e Hash Extensível em um ambiente persistente e integrado.
