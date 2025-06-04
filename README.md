# Gerenciador de Séries

Este é um sistema para acompanhamento de séries de TV que utiliza a API do TVMaze para buscar informações sobre as séries.

## Funcionalidades

### Busca e Visualização
- Busca de séries por nome
- Visualização detalhada com informações como:
  - Nome e idioma
  - Gêneros
  - Nota geral
  - Estado atual
  - Emissora
  - Data de estreia
  - Data de término

### Gerenciamento de Listas
- Categorização em:
  - Séries favoritas
  - Séries já assistidas
  - Séries para assistir
- Movimentação de séries entre categorias
- Verificação automática de duplicatas
- Remoção de séries das listas

### Sistema de Ordenação
- Ordenação bidirecional (crescente/decrescente) por:
  - Ordem alfabética
  - Nota geral
  - Estado da série
  - Data de estreia
  - Data de término
- Ordenação automática ao trocar de categoria
- Tratamento especial para valores nulos (ex: datas não informadas)

### Interface Gráfica
- Design moderno e intuitivo
- Navegação por abas para diferentes categorias
- Feedback visual para todas as operações
- Mensagens informativas para o usuário
- Botões com ícones para indicar direção da ordenação

### Persistência de Dados
- Salvamento automático ao fechar o programa
- Formato JSON para armazenamento
- Tratamento adequado de datas
- Serialização otimizada dos dados

## Requisitos

- Java 21
- Maven

## Como executar

1. Clone o repositório
2. Na pasta do projeto, execute:
   ```
   mvn clean install
   mvn exec:java -Dexec.mainClass="io.github.pablovns.Principal"
   ```

## Tecnologias utilizadas

- Java 21
- Swing (interface gráfica)
- API TVMaze
- Gson para serialização JSON
- Maven para gerenciamento de dependências

## Estrutura do projeto

- `modelo`: Classes que representam as entidades do sistema e enums para categorização
- `gui`: Classes da interface gráfica e componentes visuais
- `servico`: Classes para comunicação com a API do TVMaze
- `persistencia`: Classes para gerenciamento da persistência de dados
- `util`: Classes utilitárias e constantes

## Observações

- O sistema salva automaticamente os dados ao fechar
- Os dados são armazenados no arquivo `dados_usuario.json`
- Todas as operações possuem tratamento de exceções para evitar falhas inesperadas
- O código segue boas práticas de programação:
  - Baixo acoplamento entre componentes
  - Alta coesão nas classes
  - Uso de enums para constantes e categorização
  - Tratamento adequado de casos especiais 