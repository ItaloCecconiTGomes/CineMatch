# 🎬 Agile Movies

> **Seu próximo filme começa aqui.**

Sistema inteligente de recomendação de filmes baseado no perfil do usuário, desenvolvido em Java com foco em arquitetura limpa, testes unitários robustos e uso de Mockito para simulação de dependências externas.

---

## 📌 Sobre o Projeto

O **Agile Movies** é um recomendador de filmes que analisa o perfil cinematográfico do usuário e retorna sugestões personalizadas com base em:

* gêneros favoritos;
* duração preferida;
* classificação etária;
* idiomas aceitos;
* histórico de filmes assistidos;
* avaliações anteriores.

O sistema aplica filtros, calcula um score de compatibilidade e gera um ranking inteligente de recomendações.

Projeto desenvolvido para a disciplina de **Teste de Software**, utilizando:

* Java
* JUnit 5
* Mockito
* Maven

---

## 🧠 Como Funciona

O fluxo principal da recomendação é:

1. O usuário possui um `PerfilCinefilo`
2. O sistema busca filmes no catálogo
3. Filmes incompatíveis são filtrados
4. Cada filme recebe um score de compatibilidade
5. O sistema ordena os melhores candidatos
6. O histórico é atualizado
7. O usuário recebe suas recomendações

---

## ⚙️ Tecnologias Utilizadas

* ☕ Java 17+
* 🧪 JUnit 5
* 🎭 Mockito
* 📦 Maven
* 📊 JaCoCo
* 🧱 Orientação a Objetos

---

## 🗂️ Estrutura do Projeto

```bash
AgileMovies/
│
├── src/
│   ├── application/
│   │   └── Program.java
│   │
│   ├── exception/
│   │   ├── DuracaoInvalidaException.java
│   │   ├── PerfilIncompletoException.java
│   │   └── PesoInvalidoException.java
│   │
│   ├── model/
│   │   ├── Filme.java
│   │   ├── PerfilCinefilo.java
│   │   ├── Recomendacao.java
│   │   ├── Usuario.java
│   │   │
│   │   └── enums/
│   │       ├── ClassificacaoEtaria.java
│   │       ├── Genero.java
│   │       └── Idioma.java
│   │
│   ├── service/
│   │   ├── CalculadoraScore.java
│   │   ├── CatalogoFilmesAPI.java
│   │   ├── FiltroFilmes.java
│   │   ├── HistoricoUsuarioRepository.java
│   │   ├── NotificadorPush.java
│   │   └── RecomendadorService.java
│   │
│   ├── util/
│   │   └── GeradorAleatorio.java
│   │
│   └── testes/
│       ├── CalculadoraScoreTest.java
│       ├── FilmeTest.java
│       ├── FiltroFilmesTest.java
│       ├── PerfilCinefiloTest.java
│       └── RecomendadorServiceTest.java
│
├── pom.xml
└── README.md
```

---

## 🏗️ Arquitetura

### Camada Model

Responsável pelas entidades do domínio:

* `Usuario`
* `PerfilCinefilo`
* `Filme`
* `Recomendacao`

### Camada Service

Contém as regras de negócio:

* `RecomendadorService`
* `CalculadoraScore`
* `FiltroFilmes`

### Interfaces Mockáveis

* `CatalogoFilmesAPI`
* `HistoricoUsuarioRepository`
* `NotificadorPush`
* `GeradorAleatorio`

---

## 🎯 Regras de Recomendação

O score final varia de **0 a 100** e utiliza:

| Critério                  | Peso |
| ------------------------- | ---- |
| Compatibilidade de gênero | 50%  |
| Duração ideal             | 20%  |
| Popularidade              | 15%  |
| Afinidade histórica       | 15%  |

---

## 🔍 Regras de Filtragem

Filmes são removidos quando:

* já foram assistidos;
* possuem classificação acima da permitida;
* estão em idioma não aceito;
* possuem gênero com peso `0.0`.

---

## 🧪 Testes

O projeto possui:

* testes unitários com JUnit 5;
* mocks com Mockito;
* testes parametrizados;
* testes de integração;
* cobertura utilizando JaCoCo.

### Exemplos testados

✅ Score correto
✅ Ordenação por ranking
✅ Filtro de classificação etária
✅ Tratamento de exceções
✅ Histórico de recomendações
✅ Notificações
✅ Empates por popularidade
✅ Catálogo vazio

---

## ▶️ Como Rodar o Projeto

### Clonar o repositório

```bash
git clone https://github.com/seu-usuario/agile-movies.git
```

### Entrar na pasta

```bash
cd agile-movies
```

### Rodar o projeto

```bash
mvn clean install
```

---

## 🧪 Como Executar os Testes

```bash
mvn test
```

---

## 📊 Cobertura de Testes

Gerar relatório JaCoCo:

```bash
mvn clean test jacoco:report
```

Relatório disponível em:

```bash
target/site/jacoco/index.html
```

---

## 📸 Demonstração

### Exemplo de recomendação

Usuário:

* gosta de Ficção Científica e Drama;
* prefere filmes entre 90–150 minutos;
* aceita inglês e português.

Resultado:

1. A Chegada
2. Duna: Parte Dois
3. Ela (Her)

Com justificativa textual baseada no perfil do usuário.

---

## 👥 Integrantes

* Adailton da Cruz Silva Júnior, Italo Cecconi Teixeira Gomes e Maurício Gabriel Souza de Jesus
* 200034297 | 200034046 | 200034664

---

## 💡 Aprendizados

Durante o desenvolvimento, aprendemos:

* importância de testes unitários;
* diferença entre lógica pura e dependências externas;
* uso correto de mocks;
* arquitetura orientada a responsabilidades;
* criação de sistemas desacoplados e testáveis.

---

## 🚀 Futuras Melhorias

* integração com API real de filmes;
* sistema de login;
* recomendação baseada em humor;
* recomendação colaborativa;
* interface web/mobile;
* machine learning para personalização avançada.

---

## 📄 Licença

Projeto acadêmico desenvolvido para fins educacionais.
