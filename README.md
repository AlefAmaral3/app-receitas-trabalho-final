# App de Receitas - Trabalho Final

## Sobre a Aplicação

Esta é uma aplicação Android desenvolvida em Kotlin com Jetpack Compose que permite aos utilizadores pesquisar, guardar, criar e gerir receitas culinárias. A aplicação integra-se com a API TheMealDB para obter receitas da internet e também permite criar receitas personalizadas localmente.

## Funcionalidades

### 1. Ecrã Inicial (Home)
- Visualização de todas as receitas guardadas localmente
- Barra de pesquisa para filtrar receitas por nome
- Contador de receitas guardadas
- Possibilidade de marcar/desmarcar receitas como favoritas
- Opção de eliminar receitas criadas pelo utilizador
- Clique em qualquer receita para ver detalhes completos

### 2. Ecrã de Pesquisa (Search)
- Pesquisa de receitas através da API TheMealDB
- Resultados mostrados em tempo real
- Possibilidade de marcar receitas como favoritas diretamente da pesquisa
- Acesso aos detalhes de cada receita

### 3. Ecrã de Favoritos
- Listagem de todas as receitas marcadas como favoritas
- Acesso rápido às receitas preferidas do utilizador
- Possibilidade de remover dos favoritos

### 4. Ecrã de Criar Receita
- Formulário para criar receitas personalizadas
- Campos disponíveis:
  - Nome da receita
  - Categoria
  - Instruções de preparação
  - URL da imagem (opcional)
- As receitas criadas são guardadas localmente na base de dados

### 5. Ecrã de Detalhes
- Visualização completa da receita selecionada
- Imagem da receita em destaque
- Nome e categoria
- Instruções completas de preparação
- Botões de ação:
  - **Guardar**: guarda receitas da API localmente
  - **Editar**: permite editar receitas guardadas
  - **Voltar**: retorna ao ecrã anterior

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação
- **Jetpack Compose**: Framework moderno para UI
- **Room Database**: Persistência local de dados
- **Retrofit**: Consumo da API REST
- **Coil**: Carregamento de imagens
- **Navigation Component**: Navegação entre ecrãs
- **LiveData & ViewModel**: Arquitetura MVVM
- **Coroutines**: Programação assíncrona

## Arquitetura

O projeto segue o padrão MVVM (Model-View-ViewModel):

```
app/
├── MainActivity.kt          # Atividade principal e composables de UI
├── RecipeViewModel.kt       # Lógica de negócio e estado
├── RecipeRepository.kt      # Camada de dados (API + Database)
├── RecipeEntities.kt        # Modelo de dados (Entity Room)
├── ReceitasDao.kt          # Interface DAO do Room
├── AppDatabase.kt          # Configuração da base de dados Room
└── TheMealDbApi.kt         # Interface Retrofit para a API
```

## Base de Dados

A aplicação utiliza Room Database com a seguinte estrutura:

**Tabela: receitas**
- `id` (Int): ID local auto-incrementado
- `id_api` (String?): ID da receita na API externa
- `nome` (String): Nome da receita
- `categoria` (String): Categoria da receita
- `instrucoes` (String): Instruções de preparação
- `imagem_url` (String?): URL da imagem
- `e_favorito` (Boolean): Indicador se é favorita
- `e_criado_pelo_utilizador` (Boolean): Indica se foi criada localmente

## API Externa

A aplicação integra-se com a **TheMealDB API** através do RapidAPI:
- **Base URL**: https://themealdb.p.rapidapi.com/
- **Endpoints utilizados**:
  - `search.php?s={query}` - Pesquisa de receitas
  - `lookup.php?i={id}` - Detalhes de uma receita específica

**Nota**: A chave da API está incluída no código fonte no ficheiro `TheMealDbApi.kt`.

## Como Executar

### Pré-requisitos
- Android Studio (versão mais recente recomendada)
- JDK 11 ou superior
- Dispositivo Android (API 24+) ou emulador

### Passos
1. Clone o repositório ou faça download do código fonte
2. Abra o projeto no Android Studio
3. Aguarde o Gradle sincronizar as dependências
4. Execute a aplicação num dispositivo ou emulador Android

## Requisitos do Sistema

- **Android SDK mínimo**: 24 (Android 7.0 Nougat)
- **Android SDK alvo**: 36
- **Versão da aplicação**: 1.0

## Interface do Utilizador

A aplicação possui uma interface moderna e intuitiva com:
- Navegação por tabs na parte inferior (Bottom Navigation)
- 4 secções principais: Início, Procurar, Favoritos e Criar
- Design responsivo seguindo Material Design 3
- Suporte a tema escuro/claro (automático conforme sistema)

## Como Funciona

1. **Pesquisa de Receitas**: O utilizador pode pesquisar receitas na tab "Procurar". As receitas são obtidas em tempo real da API TheMealDB.

2. **Guardar Receitas**: Ao abrir os detalhes de uma receita da API, o utilizador pode guardá-la localmente clicando em "Guardar".

3. **Criar Receitas**: Na tab "Criar", o utilizador pode adicionar receitas personalizadas que são guardadas apenas localmente.

4. **Favoritos**: Em qualquer lista de receitas, o utilizador pode clicar no ícone de coração para marcar/desmarcar como favorita.

5. **Editar/Eliminar**: Receitas guardadas localmente podem ser editadas ou eliminadas.

## Credenciais

**Não são necessárias credenciais de utilizador.** A aplicação funciona de forma autónoma sem sistema de autenticação ou login.

## Limitações e Problemas Conhecidos

### Limitações
1. **Dependência da Internet**: A funcionalidade de pesquisa requer conexão à internet para aceder à API.

2. **API Limitada**: A API gratuita do TheMealDB tem algumas limitações:
   - Nem todas as pesquisas retornam resultados
   - Alguns campos podem vir vazios ou incompletos
   - A pesquisa funciona melhor em inglês

3. **Edição Limitada**: Apenas receitas guardadas localmente (criadas pelo utilizador ou guardadas da API) podem ser editadas.

4. **Sem Sincronização**: Os dados são guardados apenas localmente no dispositivo. Não há sincronização na cloud.

5. **Validação Básica**: A validação de formulários é mínima (apenas verifica campos não vazios).

### Problemas Conhecidos

1. **Performance com Muitas Receitas**: Com centenas de receitas guardadas, a lista pode ter scrolling menos fluido em dispositivos mais antigos.

2. **Tratamento de Erros**: Alguns erros de rede não têm mensagens explicativas detalhadas ao utilizador.

3. **URLs de Imagem**: Se o URL fornecido for inválido ao criar uma receita, a imagem pode não ser exibida.

4. **Sem Modo Offline Completo**: Receitas da API não são cacheadas automaticamente.

## Melhorias Futuras

- Sistema de categorias personalizadas
- Exportação/importação de receitas
- Partilha de receitas
- Sistema de ingredientes e quantidades
- Timer de cozinha integrado
- Lista de compras baseada em receitas
- Pesquisa avançada com filtros

## Desenvolvimento

Este projeto foi desenvolvido como trabalho final utilizando as melhores práticas de desenvolvimento Android moderno, incluindo:
- Arquitetura MVVM para separação de responsabilidades
- Injeção de dependências manual
- Programação reativa com LiveData
- UI declarativa com Jetpack Compose
- Persistência de dados com Room

---

**Versão**: 1.0  
**Data**: Janeiro 2026  
**Linguagem**: Kotlin  
**Framework**: Jetpack Compose
