# API de Biblioteca - Desafio Java Jr NT Consult

API REST para cadastro, consulta, atualização e exclusão de livros, desenvolvida com Spring Boot e banco H2 em memória. A API foi desenvolvida seguindo a metodologia TDD (*Test Driven Development*) utilizando a IDE IntelliJ Community Edition

## Tecnologias

Java 17, com Spring Boot 4.1 (Spring Web MVC), Spring Data JPA (Hibernate), Bean Validation, H2 Database, JUnit 6 e Mockito

## Execução

No Windows, use `mvnw.cmd spring-boot:run`.
A API sobe em **http://localhost:8080**.

### Configuração do Console do H2

URL do console: http://localhost:8080/h2-console <br>
JDBC URL: `jdbc:h2:mem:librarydb`           
Usuário: `sa`                      
Não há senha     

Como o banco é criado em memória quando a aplicação sobe, os dados são descartados quando ela para.

## Como rodar os testes

```bash
./mvnw test
```

## Rotas

| Método | Rota          | Descrição                              | Respostas Esperadas                              |
|--------|---------------|----------------------------------------|--------------------------------------------------|
| POST   | `/books`      | Cadastra um livro                      | `201 Created` (com header `Location`), `400`     |
| GET    | `/books`      | Lista todos os livros cadastrados      | `200 OK`                                         |
| GET    | `/books/{id}` | Busca um livro pelo id                 | `200 OK`, `404 Not Found`                        |
| PUT    | `/books/{id}` | Atualiza os dados de um livro pelo id  | `200 OK`, `400 Bad Request`, `404 Not Found`     |
| DELETE | `/books/{id}` | Exclui um livro pelo id                | `204 No Content`, `404 Not Found`                |

### Body de request (Para POST e PUT)

```json
{
  "titulo": "Dom Casmurro",
  "autor": "Machado de Assis",
  "anoPublicacao": 1899
}
```

`titulo` e `autor` são obrigatórios e não podem ser vazios. `anoPublicacao` é obrigatório e deve ser positivo. O `id` é gerado pelo banco e não é aceito no body.


## Decisões técnicas

O projeto foi gerado pelo [Spring Initializr](https://start.spring.io/)

### Spring Boot
Além de ser o padrão do mercado, entrega servidor embutido, datasource, JPA e validação, Spring Data, Bean Validation e os componentes de teste (`@WebMvcTest`, `@SpringBootTest`) sem configuração manual. 

### H2 em memória
O H2 não exige a infraestrutura que bancos de dados mais conhecidos necessitam (PostgreSQL, MySQL, Oracle, etc), além de ter sido citado no enunciado no teste como uma tecnologia possível. O banco apresentou ótima performance para os testes de integração, por ser rápido e cada execução começar do zero. Ressalta-se, entretanto, que por ser um banco de dados volátil, ele não oferece durabilidade de dados, e portanto não seria um banco para produção. Entretanto, numa eventual migração para outro banco de dados, como PostgreSQL, bastaria realizar a troca de dependência e o ajuste de URLs e credenciais do datasource, já que o acesso de ambos já passa pela JPA e Spring Data.

### Aplicação de português e inglês no código
A entidade e seus campos (`Livro`, `titulo`, `autor`, `anoPublicacao`) seguem o enunciado, que definiu esses nomes em português. Classes de infraestrutura, métodos e rotas seguem em inglês, como o próprio framework.

### DTOs com Record
A entidade JPA não é exposta na API. O contrato HTTP (`BookRequest` / `BookResponse`) é separado da estrutura da tabela, então um lado pode mudar sem quebrar o outro. Isso também impede *mass assignment*: o `BookRequest` não tem `id`, então o cliente não consegue definir ou sobrescrever o identificador. As regras de validação ficam no DTO de entrada, e não na entidade. O `record` deixa esses objetos imutáveis e concisos sem precisar de Lombok, e o `BookResponse.from(livro)` concentra a conversão em um único lugar.

### Erros com ProblemDetail ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457))
Em vez de inventar um formato próprio de erro, resolveu-se seguir o padrão da RFC 9457 (`application/problem+json`). O service lança `BookNotFoundException`, uma exceção com significado de domínio, e o `GlobalExceptionHandler` (`@RestControllerAdvice`) a traduz para HTTP. Assim, a regra de negócio não conhece status HTTP, e o tratamento de erro fica centralizado. Erros de validação (400) seguem o mesmo formato, via adição spring.mvc.problemdetails.enabled=true ao application.properties

### Níveis de teste
- **BookServiceTest**: unitário, com Mockito e sem subir o Spring.
- **BookControllerTest**: `@WebMvcTest` com o service mockado. Testa a camada HTTP: rotas, status, header `Location`, serialização do JSON e validação.
- **BookIntegrationTest**: `@SpringBootTest` com MockMvc e H2 de verdade.

Os testes foram desenvolvidos seguindo o padrão [TDD](https://www.ibm.com/br-pt/think/topics/test-driven-development)

### Dirty checking no PUT
Dentro de uma transação, a entidade carregada acaba sendo gerenciada pelo Hibernate. No commit, ele compara o estado atual com o original e gera o `UPDATE` automaticamente. Por isso o `update` só busca o livro, chama `livro.updateBook(...)` e retorna, sem `save()`. Chamar `save` numa entidade já gerenciada geraria uma redundância.

### Por que não foi usado deleteById
Desde o Spring Data 3, `deleteById` com um id inexistente não lança erro [documentação do CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html). Usado diretamente, `DELETE /books/9999` responderia `204 No Content` para um livro que nunca existiu. Por isso o `delete` busca a entidade pelo método `searchEntityById`, que lança `BookNotFoundException` e resulta em 404, e após isso acaba chamando `delete(entidade)`

## Postman
No Postman, Import → selecione o arquivo Collection [`docs/TesteNTConsult_Biblioteca_api.postman_collection.json`](docs/TesteNTConsult_Biblioteca_api.postman_collection.json). Ela cobre todas as rotas, incluindo cenários de 400 e 404. Sugere-se que as requisições sejam executadas na ordem.
