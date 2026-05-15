# Golf Office API

RESTful API under development for the operational management of a golf course. The project covers player registration, tee time scheduling, bookings, booking players, rental items, player-level payments, receipts, check-in tickets, daily cash register closing, rental damage reports, and tee time capacity rules.

The main focus of this project is Java backend development with Spring Boot, using layered architecture, DTOs, validations, business rules in services, transactions, and HATEOAS links in API responses.

## Project Status

In development.

Main features already implemented:

- Full CRUD for Players.
- Full CRUD for Tee Times.
- Full CRUD for Bookings.
- Full CRUD for Booking Players.
- Full CRUD for Rental Items.
- Full CRUD for Rental Transactions.
- Full CRUD for Payments.
- Full CRUD and automatic issuing for Receipts.
- Full CRUD and automatic issuing for Check-in Tickets.
- Full CRUD for Cash Register Closures.
- Full CRUD for Rental Damage Reports.
- User entity, encrypted passwords, and role-based access control.
- JWT authentication with access token, refresh token, refresh rotation, and logout/session revocation.
- REST API with endpoints separated by resource.
- OpenAPI/Swagger documentation with JWT Bearer authorization support.
- DTOs for input and output data.
- HATEOAS links in API responses.
- Bean Validation.
- Centralized exception handling.
- JSON responses for security errors such as `401 Unauthorized` and `403 Forbidden`.
- Business rules in the service layer.
- Tee time capacity control.
- Automatic update of `teeTime.bookedPlayers` and tee time status.
- Automatic update of booking totals and booking status.
- Individual payments per player inside a booking.
- Partial payments, refunds, and pending balance per player.
- Refund workflow that releases the player from the booking operation while preserving historical records.
- Stock control for rentable items.
- Individual, global, and automatic return of rented items.
- Individual rental return inspection with optional damage report persistence.
- Automatic scheduled return of rental items every day at midnight.
- Daily cash register closing with preview, persisted closing summary, closing items, printable report, and totals by payment method.
- Domain enums for operational statuses such as booking player, booking, tee time, rental transaction, and payment.
- MySQL integration with Spring Data JPA.
- Relational database model with JPA relationships and foreign keys for the main domain flows.
- Flyway migrations, schema validation, constraints, indexes, and initial seed data.
- Development seed user for testing authenticated endpoints.
- React + TypeScript frontend migration to consume the API.
- Legacy static frontend archived after the React migration covered the current flows.
- Docker Compose setup for Spring Boot API and MySQL.
- Integration tests for business flows and security tests with MockMvc.

## Tech Stack

- Java 21
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring HATEOAS
- Spring Validation
- Spring Scheduling
- Spring Security
- MySQL
- Flyway
- Dozer Mapper
- JWT
- Springdoc OpenAPI / Swagger UI
- JUnit
- MockMvc
- Maven
- React
- TypeScript
- Vite
- HTML
- CSS
- JavaScript

## Current Architecture

Main backend structure:

```text
src/main/java/com/project/golfofficeapi
|-- controllers
|-- dto
|-- exceptions
|-- mapper
|-- model
|-- repository
|-- security
`-- services
```

Main responsibilities:

- `controllers`: expose REST endpoints.
- `services`: centralize business rules and orchestration.
- `repository`: database access through Spring Data JPA.
- `security`: Spring Security configuration, JWT handling, authentication filters, and JSON security error handlers.
- `model`: persisted entities.
- `dto`: objects exposed by the API.
- `exceptions`: custom exceptions and global exception handling.
- `mapper`: conversion between entities and DTOs.

The API keeps DTOs simple by exposing IDs such as `bookingId`, `playerId`, `teeTimeId`, and `bookingPlayerId`, while the persistence layer now uses JPA relationships between the main entities. This keeps the REST contract easier to consume and the database model more professional.

Operational statuses are represented with Java enums instead of loose strings. This reduces typo-related bugs, keeps valid states centralized in the backend, and makes business rules easier to evolve. The database stores these enum values as readable strings, preserving clarity in SQL and avoiding fragile numeric ordinal values.

## Security Architecture

The API is protected with Spring Security using stateless JWT authentication.

Main security flow:

```text
User credentials
-> POST /auth/login
-> backend validates email and encrypted password
-> backend returns short-lived access token and refresh token
-> client sends Authorization: Bearer <accessToken>
-> JwtAuthenticationFilter validates token on protected requests
-> Spring Security applies role rules
-> backend returns data, 401, or 403
```

Access tokens are short-lived and are not stored in the database. They are signed JWTs used to authenticate API requests.

Refresh tokens are stored in the database only as SHA-256 hashes. The raw refresh token is returned to the client once, while the database keeps only the hash. This prevents direct token reuse if the database is exposed.

Refresh token rotation is enabled:

- `POST /auth/refresh` validates the current refresh token.
- The current refresh token is revoked.
- A new access token is issued.
- A new refresh token is issued.

Logout revokes the refresh token:

```http
POST /auth/logout
```

After logout, that refresh token can no longer be used to renew the session.

### Roles

Current roles:

- `MANAGER`
- `RECEPTIONIST`

Current authorization rules:

- Public endpoints: `/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`, and `/swagger-ui.html`.
- `MANAGER` can access all protected operational endpoints.
- `RECEPTIONIST` can access the daily operational flow, such as players, agenda, bookings, check-ins, rentals, payments, and receipts.
- `DELETE` operations are restricted to `MANAGER`.
- Rental item creation and updates, such as stock and price maintenance, are restricted to `MANAGER`.
- Cash register closure endpoints are restricted to `MANAGER`.

Security error responses are JSON and follow the same API error format:

```json
{
  "timestamp": "2026-05-15T20:52:00.000+00:00",
  "message": "Unauthorized",
  "details": "uri=/player"
}
```

`401 Unauthorized` means the request is not authenticated or the token is invalid.

`403 Forbidden` means the user is authenticated, but does not have the required role.

### Development Test User

Flyway creates a development/demo manager user to make the API easy to test after cloning the repository and running the migrations.

```text
Email: manager@golfoffice.dev
Password: admin123
Role: MANAGER
```

This user is intended for local development and portfolio demonstration, not production credentials.

### Swagger Authentication

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

How to test authenticated endpoints in Swagger:

1. Run the backend.
2. Open Swagger UI.
3. Execute `POST /auth/login` with the development user.
4. Copy the `accessToken` from the response.
5. Click **Authorize**.
6. Paste only the token value, without writing `Bearer`.
7. Confirm.
8. Call protected endpoints normally.

Swagger automatically sends:

```http
Authorization: Bearer <accessToken>
```

The JWT button is generated by the OpenAPI security scheme configured in `OpenApiConfig`.

## Implemented Resources

### Authentication

Authentication and session renewal endpoints.

Base endpoint:

```http
/auth
```

Operations:

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

Highlights:

- Login with email and password.
- Passwords stored encrypted with Spring Security password encoding.
- JWT access token returned after successful login.
- Refresh token returned after successful login.
- Refresh token stored in the database as a hash.
- Refresh token rotation on `/auth/refresh`.
- Logout revokes the current refresh token.

### Players

Player registration and management.

Base endpoint:

```http
/player
```

Operations:

- `GET /player`
- `GET /player/{id}`
- `GET /player/search?name={name}`
- `POST /player`
- `PUT /player`
- `DELETE /player/{id}`

### Tee Times

Management of available course start times.

Base endpoint:

```http
/tee-time
```

Highlights:

- Play date with `LocalDate`.
- Start time with `LocalTime`.
- Default player limit per tee time.
- Booked player control.
- Tee time status.
- Base green fee calculated automatically in the backend.
- Constraint to prevent duplicate tee times for the same date and start time.

### Bookings

Bookings linked to a tee time.

Base endpoint:

```http
/booking
```

Highlights:

- Automatic booking code generation.
- Automatic creation date and time.
- Initial status controlled by the backend.
- `createdBy` prepared for future user integration.
- Booking total controlled automatically as players and rentals are added.
- Booking status is automatically confirmed when every player is checked in and fully paid.

### Booking Players

Link between a booking and the players joining that tee time.

Base endpoint:

```http
/booking-player
```

Highlights:

- Add players to a booking.
- Allows the same player more than once in a booking, supporting member-with-guests scenarios.
- Supports group/operator bookings through `playerCount`, allowing one booking player row to represent up to 4 players.
- Green fee automatically filled from the tee time.
- Check-in per player.
- Automatic check-in ticket generation when a player is checked in.
- Automatic check-in ticket cancellation when check-in is undone.
- Booking player statuses: `ACTIVE`, `REFUNDED`, and `CANCELLED`.
- Tee time capacity validation.
- Automatic update of `teeTime.bookedPlayers`.
- Automatic update of tee time status.
- Automatic recalculation of `booking.totalAmount`.
- Refunded booking players are removed from operational capacity and totals, but kept in the database for payment, receipt, rental, and ticket history.
- Uses `@Transactional` to keep booking, booking player, and tee time data consistent.

### Check-in Tickets

Printable ticket generated for the player after check-in.

Base endpoint:

```http
/check-in-ticket
```

Highlights:

- Ticket linked to `bookingPlayerId`.
- Automatic ticket number generation using a progressive format such as `CT-2026-000001`.
- Player and booking code snapshots for historical consistency.
- Starting tee fixed by business rule as `TEE 1`.
- Crossing tee fixed by business rule as `TEE 10`.
- Crossing time automatically calculated as the tee time plus 2 hours and 15 minutes.
- Active ticket reuse to avoid duplicate active tickets for the same checked-in player.
- Ticket cancellation when check-in is undone.
- Printable preview available in the React Agenda.

### Rental Items

Registration and stock control for rentable items, such as buggies, trolleys, and equipment.

Base endpoint:

```http
/rental-item
```

Highlights:

- Full CRUD for rental items.
- Total stock control.
- Available stock control.
- Rental price per item.
- Activate and deactivate rental items.
- Validation to prevent available stock from being greater than total stock.

### Rental Transactions

Rental item transactions assigned to a player inside a booking.

Base endpoint:

```http
/rental-transaction
```

Highlights:

- Full CRUD for rental transactions.
- Rental assigned to `bookingPlayerId`.
- Validation to ensure the booking player belongs to the booking.
- Automatic stock decrease when renting an item.
- Item return with stock restoration.
- Endpoint to return all pending rental items.
- Individual return inspection in the React Agenda: the attendant must choose between returning the item in good condition or registering damage.
- Damaged rentals are marked as `DAMAGED` and remain outside available stock.
- Automatic scheduled return of rentals in `RENTED` status every day at midnight.
- Prevents deleting an active rental transaction before returning or cancelling it.
- Automatic inclusion of rentals in the booking total.
- Pricing rule for buggies and electric trolleys based on twilight and member discounts.

### Rental Damage Reports

Historical record for damaged rental items found during item return.

Base endpoint:

```http
/rental-damage-report
```

Operations:

- `GET /rental-damage-report`
- `GET /rental-damage-report/{id}`
- `GET /rental-damage-report/status/{status}`
- `GET /rental-damage-report/rental-item/{rentalItemId}`
- `GET /rental-damage-report/rental-transaction/{rentalTransactionId}`
- `POST /rental-damage-report`
- `PUT /rental-damage-report`
- `PUT /rental-damage-report/{id}/resolve`
- `DELETE /rental-damage-report/{id}`

Highlights:

- Damage reports can be linked to a rental transaction and/or rental item.
- Used by the React Agenda when returning one rented item with damage.
- Used by the React Materials page when returning all materials at the end of the day and entering damage notes.
- Statuses: `OPEN`, `RESOLVED`, and `CANCELLED`.
- Delete behaves as cancellation, preserving the historical report.
- Provides a future-ready flow for maintenance, repair, replacement, or billing decisions.

### Payments

Individual payments per player inside a booking.

Base endpoint:

```http
/payment
```

Highlights:

- Full CRUD for payments.
- Payment linked to `bookingId` and `bookingPlayerId`.
- Find payments by booking.
- Find payments by booking player.
- Payment statuses: `PENDING`, `PAID`, `REFUNDED`, and `CANCELLED`.
- Automatic `paidAt` registration when the payment status is `PAID`.
- Validation to prevent payment above the player amount due.
- Partial payment support per player.
- Refund support with automatic operational release of the player.
- When a payment is refunded, the backend returns that player's rental items to stock, marks rentals as returned, cancels the active check-in ticket, undoes check-in, marks the booking player as `REFUNDED`, and recalculates booking and tee time capacity.
- If the refunded player was the last active player in the booking, the booking is marked as `CANCELLED` so the agenda slot becomes available again.
- Integration with automatic booking confirmation.

### Receipts

Historical receipt document generated from player-level payments.

Base endpoint:

```http
/receipt
```

Highlights:

- Receipt linked to `bookingId`, `bookingPlayerId`, and `paymentId`.
- Automatic receipt number generation using a progressive format such as `RC-2026-000001`.
- Player name and tax number snapshots for historical consistency.
- Booking code, play date, tee time, payment method, and payment status snapshots.
- Receipt items generated from green fee and rental payment data.
- Receipt cancellation instead of destructive deletion.
- Printable preview available in the React Agenda.

### Cash Register Closures

Daily cash register closing generated from payments, refunds, receipts, pending bookings, and rental alerts.

Base endpoint:

```http
/cash-register-closure
```

Operations:

- `GET /cash-register-closure`
- `GET /cash-register-closure/{id}`
- `GET /cash-register-closure/date/{businessDate}`
- `GET /cash-register-closure/preview?date={businessDate}`
- `POST /cash-register-closure`
- `POST /cash-register-closure/close`
- `PUT /cash-register-closure`
- `DELETE /cash-register-closure/{id}`

Highlights:

- Preview calculates the daily closing without saving it.
- Close persists the daily closing summary and its historical items.
- Totals by payment method: cash, card, MB Way, and transfer.
- Gross, refunded, and net totals.
- Counts paid payments, refunded payments, issued receipts, cancelled receipts, pending bookings, and unreturned rentals.
- Closing items preserve payment, refund, receipt, cancelled receipt, pending booking, and unreturned rental details.
- Prevents closing the same business date twice with status `CLOSED`.
- Uses internal calculation classes instead of DTOs as business-rule draft objects.
- Printable report available in the React Cash Register page.

Cash register closing items are stored in:

```http
/cash-register-closure-item
```

These records are generated by the backend when closing the cash register and represent the historical details of that closing.

## Frontend

The active frontend is the React + TypeScript app inside `frontend`.

### Legacy Static Frontend

Archived location:

```text
archive/legacy-static-frontend
```

This panel was initially created to consume and test the API directly through Spring Boot. It has been archived because the React frontend now covers the current operational flows: Players, Agenda, booking players, rentals, payments, receipts, check-in tickets, materials, cash register closing, and rental damage reports.

Note: this frontend was created with support from the Codex agent. I have JavaScript basics, but it is not my main stack; the interface was included mainly to make the RESTful API flows easier to visualize, test, and understand.

### React + TypeScript Frontend

Location:

```text
frontend
```

During development, backend and frontend run separately:

```text
Spring Boot backend: http://localhost:8080
React/Vite frontend: http://localhost:5173
```

Vite is configured with a proxy. Frontend calls to `/api` are forwarded to the backend:

```text
/api/player   -> http://localhost:8080/player
/api/booking  -> http://localhost:8080/booking
/api/payment  -> http://localhost:8080/payment
```

Current React migration status:

- React + TypeScript structure created with Vite.
- `apiClient` layer created to consume the Spring Boot API through `/api`.
- TypeScript types created for the main entities.
- Services created for Players, Tee Times, Bookings, Booking Players, Rental Items, Rental Transactions, and Payments.
- Services created for Receipts, Receipt Items, and Check-in Tickets.
- Players page migrated to React.
- Players page includes search by name, search by ID, listing, CRUD, and member filtering.
- Materials page migrated to React.
- Agenda page migrated to React.
- Daily agenda with slots from 07:00 to 19:00 every 10 minutes.
- Tee time and booking creation/selection when clicking a time slot.
- Booking panel with internal tabs: Summary, Players, Materials, and Payments.
- Players tab with add/remove player, check-in, and player totals.
- Check-in ticket preview and printing from the Players tab.
- Materials tab with rental per player, return inspection, damage report creation, edit, delete, and stock handling.
- Payments tab with partial payment, edit, delete, refund, and pending balance.
- Refunded players/bookings are removed from the operational agenda view while historical data remains available through backend records.
- Receipt preview and printing from the Payments tab.
- Cash Register page with daily preview, persisted closing, totals by payment method, alerts, closing items, and printable report.
- Materials page with end-of-day return-all action and persisted damage report notes.
- Main navigation with Players, Agenda, Materials, and Cash Register.

## Running the Backend

Requirements:

- Java 21
- MySQL running
- `golf_api` database created

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Run backend tests:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

## Running With Docker

The project can also run with Docker Compose using:

- Spring Boot API container
- MySQL container
- Flyway migrations on application startup
- Persistent MySQL volume

Requirements:

- Docker
- Docker Compose

From the project root:

```bash
docker compose up --build
```

On Windows PowerShell:

```powershell
docker compose up --build
```

Docker services:

```text
API:   http://localhost:8080
MySQL: localhost:3307
```

Inside Docker, the API connects to MySQL using the service name:

```text
mysql:3306
```

Useful commands:

```bash
docker compose ps
docker compose logs -f api
docker compose logs -f mysql
docker compose down
```

To stop containers and remove the MySQL volume, deleting the Docker database data:

```bash
docker compose down -v
```

The Docker setup uses environment variables in `application.yaml`:

```yaml
DB_URL
DB_USERNAME
DB_PASSWORD
JPA_DDL_AUTO
JPA_SHOW_SQL
JWT_SECRET
JWT_ACCESS_TOKEN_EXPIRATION_MINUTES
JWT_REFRESH_TOKEN_EXPIRATION_HOURS
```

This keeps local IDE execution and Docker execution compatible.

## Running the React Frontend

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

Production build:

```bash
npm run build
```

Note: to test the React frontend, keep the backend running at `http://localhost:8080`, because Vite uses a proxy to consume the API.

## Database

The project uses MySQL.

Current configuration:

```text
src/main/resources/application.yaml
```

Expected database:

```text
golf_api
```

Hibernate is configured with:

```yaml
spring.jpa.hibernate.ddl-auto: validate
```

Flyway is responsible for the database structure. Hibernate validates that the entity model matches the schema instead of changing the schema automatically.

The project uses Flyway for versioned migrations, constraints, indexes, foreign keys, and seed data.

Current migration highlights:

- Initial player seed data.
- Foreign keys for Booking Player, Booking, Rental Transaction, Payment, Receipt, and Receipt Item flows.
- Final constraints and indexes for the relational model.
- Check-in ticket table.
- Booking player count and booking player status migrations.
- Cash register closure and cash register closure item tables.
- Rental damage report table with foreign keys to rental transaction and rental item.
- User table with role, encrypted password, active flag, and timestamps.
- Refresh token table storing token hashes, expiration, and revocation timestamp.
- Development manager seed user for local/demo authentication.
- Migration from legacy `RECEPTION` role name to `RECEPTIONIST`.

## Roadmap

Recently implemented roadmap items:

- React + TypeScript base migration with Vite.
- TypeScript services to consume Spring Boot API endpoints.
- React Players page.
- React Materials page.
- React Agenda page.
- Booking panel with tabs inside the Agenda.
- Booking Summary tab.
- Booking Players tab.
- Booking Materials tab.
- Booking Payments tab.
- Automatic frontend data refresh after important actions.
- Automatic backend return of rented items at midnight.
- JPA relationship migration for the main domain entities while keeping REST DTOs with IDs.
- Flyway foreign keys, constraints, indexes, and Hibernate schema validation.
- Automatic receipt issuing from paid player-level payments.
- Printable receipt preview in the React Agenda.
- Automatic check-in ticket issuing from player check-in.
- Printable check-in ticket preview in the React Agenda.
- Group booking support with player count per booking player.
- Refund flow with rental return, check-in cancellation, booking player release, booking cancellation when empty, and tee time capacity recalculation.
- Java enums for core operational statuses.
- Players member filter in the React frontend.
- Cash Register backend module with preview, close, persisted summary, items, and printable React report.
- Rental damage report backend module and React integration.
- Individual rental return inspection in the Agenda.
- End-of-day return-all workflow in Materials with persisted damage notes.
- OpenAPI/Swagger documentation with JWT Bearer authorization button.
- Spring Security authentication and authorization.
- User entity with encrypted passwords and roles.
- JWT access token flow.
- Refresh token persistence, rotation, and logout revocation.
- JSON security error handlers for `401 Unauthorized` and `403 Forbidden`.
- Role rules for `MANAGER` and `RECEPTIONIST`.
- Development manager seed user for local testing.
- MockMvc security integration tests.
- Docker Compose for API and MySQL.

Planned future implementations:

- Real usage of `createdBy` and `closedBy` with authenticated users.
- React login/session handling using the JWT endpoints.
- Frontend role-aware screens and actions.
- Admin/maintenance view for resolving rental damage reports.
- Optional billing flow for damaged or lost rental items.
- Pricing rule evolution with professional price, season, and twilight configuration.
- Additional custom mappers as the domain grows.
- Unit tests with JUnit and Mockito.
- More integration tests for main business flows.
- Final React frontend build served by Spring Boot.

## Validation

Commands used to validate the current state:

```bash
npm run build
```

```powershell
.\mvnw.cmd test
```

Current backend validation includes application context tests, service integration tests, cash register and payment/receipt flows, and MockMvc security tests for login, public endpoint access, protected endpoint without token, protected endpoint with valid token, and forbidden access by role.

The project does not have a `lint` script in `frontend/package.json` yet.

## License

This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for more details.
