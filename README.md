# Golf Office API

RESTful API em desenvolvimento para gestão operacional de um campo de golfe, cobrindo cadastro de jogadores, agenda de tee times, reservas, jogadores por reserva e regras de capacidade.

O projeto está sendo construído com foco em boas práticas de backend usando Spring Boot, organização por camadas, DTOs, validações, regras de negócio em services e respostas com links HATEOAS.

## Status do projeto

Em desenvolvimento.

Funcionalidades principais já implementadas:

- CRUD completo de Players.
- CRUD completo de Tee Times.
- CRUD completo de Bookings.
- CRUD completo de Booking Players.
- API REST com endpoints separados por recurso.
- DTOs para entrada e saída de dados.
- HATEOAS nos retornos da API.
- Validações com Bean Validation.
- Tratamento centralizado de exceções.
- Regras de negócio em camada de service.
- Integração com banco MySQL via Spring Data JPA.
- Painel frontend estático para consumir e testar a API.

## Tecnologias usadas

- Java 21
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring HATEOAS
- Spring Validation
- MySQL
- Flyway
- Dozer Mapper
- Maven
- HTML
- CSS
- JavaScript

## Arquitetura atual

O projeto segue uma estrutura simples e evolutiva:

```text
src/main/java/com/project/golfofficeapi
├── controllers
├── dto
├── exceptions
├── mapper
├── model
├── repository
└── services
```

Responsabilidades principais:

- `controllers`: expõem os endpoints REST.
- `services`: concentram regras de negócio e orquestração.
- `repository`: acesso ao banco via Spring Data JPA.
- `model`: entidades persistidas.
- `dto`: objetos expostos pela API.
- `exceptions`: exceções customizadas e tratamento global.
- `mapper`: conversão entre entidade e DTO.

## Recursos implementados

### Players

Cadastro e manutenção de jogadores.

Endpoint base:

```http
/player
```

Operações:

- `GET /player`
- `GET /player/{id}`
- `POST /player`
- `PUT /player`
- `DELETE /player/{id}`

### Tee Times

Gerenciamento dos horários disponíveis para saída no campo.

Endpoint base:

```http
/tee-time
```

Pontos fortes já implementados:

- Data do jogo com `LocalDate`.
- Horário de saída com `LocalTime`.
- Limite padrão de jogadores por tee time.
- Controle de jogadores reservados.
- Status do tee time.
- Green fee base calculado automaticamente no backend.
- Restrição para evitar duplicidade de tee time na mesma data e horário.

### Bookings

Reservas vinculadas a um tee time.

Endpoint base:

```http
/booking
```

Pontos fortes já implementados:

- Geração automática de código da reserva.
- Data e hora de criação automáticas.
- Status inicial controlado pelo backend.
- `createdBy` preparado para futura integração com usuários.
- Total da reserva controlado automaticamente conforme jogadores são adicionados.

### Booking Players

Vínculo entre uma reserva e os jogadores que participarão daquele horário.

Endpoint base:

```http
/booking-player
```

Pontos fortes já implementados:

- Adição de jogadores a um booking.
- Permite o mesmo player mais de uma vez no booking, para cenários como membro com guests.
- Green fee preenchido automaticamente com base no tee time.
- Check-in por jogador.
- Validação de capacidade do tee time.
- Atualização automática de `teeTime.bookedPlayers`.
- Atualização automática do status do tee time.
- Recalculo automático de `booking.totalAmount`.
- Uso de transações com `@Transactional` para manter consistência entre booking, booking player e tee time.

## Frontend auxiliar

Além da API, o projeto possui um frontend estático simples em:

```text
src/main/resources/static
```

Ele serve como painel inicial para consumir a API durante o desenvolvimento.

Observação: o frontend foi criado com apoio do agente Codex. Tenho noções de JavaScript, mas essa não é minha stack principal; a interface foi incluída principalmente para facilitar a visualização, o teste e a compreensão dos fluxos da RESTful API.

Funcionalidades disponíveis no painel:

- CRUD de Players.
- Agenda diária com horários de 07:00 até 19:00, de 10 em 10 minutos.
- Criação de tee time e booking ao clicar em um horário livre.
- Seleção de booking existente pela agenda.
- Adição de jogadores à reserva.
- Check-in de jogadores.
- Visualização da última requisição e última resposta JSON.

## Banco de dados

O projeto utiliza MySQL.

Configuração atual em:

```text
src/main/resources/application.yaml
```

Banco esperado:

```text
golf_api
```

O Hibernate está configurado com:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

Isso permite evoluir as tabelas automaticamente durante esta fase de desenvolvimento.

## Como executar

Pré-requisitos:

- Java 21
- MySQL em execução
- Banco `golf_api` criado

Executar a aplicação:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Rodar os testes:

```bash
./mvnw test
```

No Windows:

```bash
.\mvnw.cmd test
```

Após subir a aplicação, o painel web pode ser acessado em:

```text
http://localhost:8080
```

## Roadmap

Implementações futuras planejadas:

- CRUD de Rental Items.
- CRUD de Rental Transactions.
- Controle de estoque de itens alugáveis.
- Inclusão automática de alugueres no total do booking.
- CRUD de Payments.
- Regras de pagamento e status financeiro da reserva.
- Entidade User.
- Autenticação e autorização.
- Uso real de `createdBy` com usuário autenticado.
- Perfis de acesso, como admin e operador.
- Evolução das regras de pricing.
- Cadastro profissional de preços, temporadas e twilight.
- Migrações Flyway versionadas.
- Testes unitários e de integração mais completos.
- Documentação OpenAPI/Swagger.
- Melhorias no frontend ou separação futura para um app frontend dedicado.

## Licença

Este projeto está licenciado sob a licença MIT.

Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.
