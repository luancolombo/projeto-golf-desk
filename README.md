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
- CRUD completo de Rental Items.
- CRUD completo de Rental Transactions.
- CRUD completo de Payments.
- API REST com endpoints separados por recurso.
- DTOs para entrada e saída de dados.
- HATEOAS nos retornos da API.
- Validações com Bean Validation.
- Tratamento centralizado de exceções.
- Regras de negócio em camada de service.
- Controle de estoque de materiais alugáveis.
- Pagamentos individuais por jogador dentro do booking.
- Atualização automática de totais e status de booking.
- Integração com banco MySQL via Spring Data JPA.
- Painel frontend estático para consumir e testar a API.
- Base de frontend React + TypeScript em migração.

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
- React
- TypeScript
- Vite

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
- Total da reserva controlado automaticamente conforme jogadores e alugueres são adicionados.
- Status da reserva confirmado automaticamente quando todos os jogadores fazem check-in e estão pagos.

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

### Rental Items

Cadastro e controle de materiais alugáveis, como buggy, trolley e equipamentos.

Endpoint base:

```http
/rental-item
```

Pontos fortes já implementados:

- CRUD completo de materiais.
- Controle de estoque total.
- Controle de estoque disponível.
- Preço de aluguer por material.
- Ativação e desativação de material.
- Validação para evitar estoque disponível maior que estoque total.

### Rental Transactions

Lançamento de materiais alugados por jogador dentro de um booking.

Endpoint base:

```http
/rental-transaction
```

Pontos fortes já implementados:

- CRUD completo de transações de aluguer.
- Associação do aluguer ao `bookingPlayerId`.
- Validação para garantir que o jogador pertence ao booking.
- Baixa automática de estoque ao alugar material.
- Devolução de material com retorno ao estoque.
- Endpoint para devolver todos os materiais pendentes.
- Bloqueio para excluir transação ativa sem devolver ou cancelar antes.
- Inclusão automática dos alugueres no total do booking.
- Regra de preço para buggy e trolley elétrico considerando twilight e membro.

### Payments

Pagamentos individuais por jogador dentro de um booking.

Endpoint base:

```http
/payment
```

Pontos fortes já implementados:

- CRUD completo de pagamentos.
- Pagamento vinculado a `bookingId` e `bookingPlayerId`.
- Busca de pagamentos por booking.
- Busca de pagamentos por jogador do booking.
- Status de pagamento: `PENDING`, `PAID`, `REFUNDED` e `CANCELLED`.
- Registro automático de `paidAt` quando o pagamento fica como `PAID`.
- Validação para impedir pagamento acima do total devido pelo jogador.
- Suporte a pagamento parcial por jogador.
- Integração com a confirmação automática do booking.

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
- Controle de materiais alugados por jogador.
- Devolução individual ou geral de materiais alugados.
- Controle de pagamentos por jogador.
- Aba de Materiais para consultar e manter estoque, preço e status.
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

## Frontend React + TypeScript

Uma nova base de frontend está sendo criada em:

```text
frontend
```

Durante o desenvolvimento, o backend Spring Boot e o frontend React rodam em servidores separados:

```text
Backend:  http://localhost:8080
Frontend: http://localhost:5173
```

Instalar dependências do frontend:

```bash
cd frontend
npm install
```

Executar o frontend em modo desenvolvimento:

```bash
npm run dev
```

Gerar build de produção:

```bash
npm run build
```

O Vite está configurado com proxy para o backend. Chamadas feitas pelo frontend para:

```text
/api/player
/api/booking
/api/payment
```

são encaminhadas para:

```text
http://localhost:8080/player
http://localhost:8080/booking
http://localhost:8080/payment
```

Estado atual da migração React:

- Estrutura React + TypeScript criada com Vite.
- Camada `apiClient` criada para consumir a API Spring Boot via `/api`.
- Tipos TypeScript criados para as principais entidades.
- Services criados para Players, Tee Times, Bookings, Booking Players, Rental Items, Rental Transactions e Payments.
- Tela de Players migrada para React.
- Tela de Materiais migrada para React.

Enquanto a migração não estiver completa, o frontend estático antigo continua em:

```text
src/main/resources/static
```

## Roadmap

Implementações futuras planejadas:

- Módulo de Fecho de Caixa.
- Preview de fechamento diário com totais por método de pagamento.
- Validação de pendências antes de fechar o caixa, como materiais não devolvidos e pagamentos pendentes.
- Entidade User.
- Autenticação e autorização com Spring Security.
- Uso real de `createdBy` e `closedBy` com usuário autenticado.
- Perfis de acesso, como admin e operador.
- Evolução das regras de pricing.
- Cadastro profissional de preços, temporadas e twilight.
- Migrações Flyway versionadas para schema completo.
- Testes unitários com JUnit e Mockito.
- Testes de integração para fluxos principais.
- Documentação OpenAPI/Swagger.
- Docker Compose para subir API e MySQL.
- Continuação da migração do frontend para React + TypeScript.
- Migração da Agenda para React com painel de booking em abas.
- Migração de Rentals e Payments para React.
- Remoção ou arquivamento do frontend estático antigo após a migração completa.

## Licença

Este projeto está licenciado sob a licença MIT.

Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.
