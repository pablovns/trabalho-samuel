# Gerenciador de Séries

Este é um sistema para acompanhamento de séries de TV que utiliza a API do TVMaze para buscar informações sobre as séries.

## Funcionalidades

- Busca de séries por nome
- Gerenciamento de listas:
  - Séries favoritas
  - Séries já assistidas
  - Séries para assistir
- Ordenação das listas por:
  - Ordem alfabética
  - Nota geral
  - Estado da série
  - Data de estreia
- Persistência de dados em formato JSON
- Interface gráfica intuitiva

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
- JSON para persistência de dados

## Estrutura do projeto

- `modelo`: Classes que representam as entidades do sistema
- `gui`: Classes da interface gráfica
- `servico`: Classes para comunicação com a API do TVMaze
- `persistencia`: Classes para gerenciamento da persistência de dados

## Observações

- O sistema salva automaticamente os dados ao fechar
- Os dados são armazenados no arquivo `dados_usuario.json`
- Todas as operações possuem tratamento de exceções para evitar falhas inesperadas 