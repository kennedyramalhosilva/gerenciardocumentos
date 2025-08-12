# Sistema de Gerenciamento de Documentos

Projeto backend em Java 21 com Spring Boot para gerenciar documentos (upload, download, atualização e exclusão).

## Tecnologias usadas

- Java 21  
- Spring Boot 3.5.4  
- Spring Data JPA  
- Maven  
- Log4j2 para logs  
- Spring Security com API key para proteção da API  
- SpringDoc OpenAPI (Swagger) para documentação automática  
- PostgreSQL como banco de dados  
- TDD com JUnit e Mockito  

## Funcionalidades

- Criar documento (salva registro no banco e arquivo no disco)  
- Retornar documento para preview e download  
- Atualizar documento (substituir arquivo)  
- Apagar documento (remove registro e arquivo)  
- Proteção da API via API key configurada no header `X-API-KEY`  
- Tarefa agendada diária que registra no log o total de arquivos e espaço ocupado 
- Documentação automática da API via Swagger/OpenAPI, disponível em `/swagger-ui.html` ou `/swagger-ui/index.html`  
- Testes automatizados no pacote `com.sistema.gerenciardocumentos.service` usando JUnit e Mockito  

## Endpoints principais

- `POST /documentos` — criar documento (params: nome, arquivo)  
- `GET /documentos/{id}/preview` — visualizar arquivo no browser  
- `GET /documentos/{id}/download` — baixar arquivo  
- `PUT /documentos/{id}` — atualizar arquivo do documento  
- `DELETE /documentos/{id}` — deletar documento  

## Como rodar

1. Configure o arquivo `src/main/resources/application.properties` com:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/DOCUMENTOS
spring.datasource.username=postgres
spring.datasource.password=xxxx
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

app.upload-dir=uploads
api.key=xxx
logging.config=classpath:log4j2.xml
```

2. Rode a aplicação com Maven:

```
mvn spring-boot:run
```

3. Acesse a API em:

```
http://localhost:8080/documentos
```

4. A documentação Swagger estará disponível em:

```
http://localhost:8080/swagger-ui.html
```
ou
```
http://localhost:8080/swagger-ui/index.html
```

## Segurança

- A API exige que todas as requisições tenham o header `X-API-KEY` com o valor configurado em `application.properties` (`chave123` no exemplo).  
- Exceções são feitas para os endpoints do Swagger para facilitar testes e acesso à documentação.

## Testes

- Estão no pacote `com.sistema.gerenciardocumentos.service`  
- Usam JUnit e Mockito para mocks  
- Para rodar:

```
mvn test
```

## Observações

- Os arquivos são armazenados no sistema local, na pasta configurada por `app.upload-dir`.  
- A configuração de segurança é simples, baseada em filtro que valida API key.  
- Logs são feitos com Log4j2, com mensagens para criação, atualização e exclusão de documentos.  

---

### Contato

Projeto feito para avaliação de backend Java.

---
