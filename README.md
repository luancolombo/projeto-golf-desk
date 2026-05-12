# Golf Office API

RESTful API under development for the operational management of a golf course. The project covers player registration, tee time scheduling, bookings, booking players, rental items, player-level payments, and tee time capacity rules.

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
- REST API with endpoints separated by resource.
- DTOs for input and output data.
- HATEOAS links in API responses.
- Bean Validation.
- Centralized exception handling.
- Business rules in the service layer.
- Tee time capacity control.
- Automatic update of `teeTime.bookedPlayers` and tee time status.
- Automatic update of booking totals and booking status.
- Individual payments per player inside a booking.
- Partial payments, refunds, and pending balance per player.
- Stock control for rentable items.
- Individual, global, and automatic return of rented items.
- Automatic scheduled return of rental items every day at midnight.
- MySQL integration with Spring Data JPA.
- Flyway migrations and initial seed data.
- React + TypeScript frontend migration to consume the API.
- Legacy static frontend temporarily kept in `src/main/resources/static`.

## Tech Stack

- Java 21
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring HATEOAS
- Spring Validation
- Spring Scheduling
- MySQL
- Flyway
- Dozer Mapper
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
`-- services
```

Main responsibilities:

- `controllers`: expose REST endpoints.
- `services`: centralize business rules and orchestration.
- `repository`: database access through Spring Data JPA.
- `model`: persisted entities.
- `dto`: objects exposed by the API.
- `exceptions`: custom exceptions and global exception handling.
- `mapper`: conversion between entities and DTOs.

## Implemented Resources

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
- Green fee automatically filled from the tee time.
- Check-in per player.
- Tee time capacity validation.
- Automatic update of `teeTime.bookedPlayers`.
- Automatic update of tee time status.
- Automatic recalculation of `booking.totalAmount`.
- Uses `@Transactional` to keep booking, booking player, and tee time data consistent.

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
- Automatic scheduled return of rentals in `RENTED` status every day at midnight.
- Prevents deleting an active rental transaction before returning or cancelling it.
- Automatic inclusion of rentals in the booking total.
- Pricing rule for buggies and electric trolleys based on twilight and member discounts.

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
- Refund support.
- Integration with automatic booking confirmation.

## Frontend

The project currently has two frontends during the migration phase.

### Legacy Static Frontend

Location:

```text
src/main/resources/static
```

This panel was initially created to consume and test the API directly through Spring Boot. It remains temporarily available while the React migration is not fully finished.

URL when the backend is running:

```text
http://localhost:8080
```

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
- Players page migrated to React.
- Materials page migrated to React.
- Agenda page migrated to React.
- Daily agenda with slots from 07:00 to 19:00 every 10 minutes.
- Tee time and booking creation/selection when clicking a time slot.
- Booking panel with internal tabs: Summary, Players, Materials, and Payments.
- Players tab with add/remove player, check-in, and player totals.
- Materials tab with rental per player, return, edit, delete, and stock handling.
- Payments tab with partial payment, edit, delete, refund, and pending balance.
- Main navigation with Players, Agenda, Materials, and Cash Register as a future module.

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
spring.jpa.hibernate.ddl-auto: update
```

This allows the schema to evolve automatically during this development phase.

The project also uses Flyway for versioned migrations and seed data.

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
- Main navigation prepared for a future Cash Register module.

Planned future implementations:

- Cash Register / Daily Closing module.
- Daily closing preview with totals by payment method.
- Pending validation before closing the cash register, such as unreturned rental items and unpaid balances.
- User entity.
- Authentication and authorization with Spring Security.
- Real usage of `createdBy` and `closedBy` with authenticated users.
- Access profiles, such as admin and operator.
- Pricing rule evolution.
- Professional price, season, and twilight configuration.
- Full schema versioning with Flyway migrations.
- Unit tests with JUnit and Mockito.
- Integration tests for main business flows.
- OpenAPI/Swagger documentation.
- Docker Compose for API and MySQL.
- Final React frontend build served by Spring Boot.
- Removal or archival of the legacy static frontend after migration is complete.

## Validation

Commands used to validate the current state:

```bash
npm run build
```

```powershell
.\mvnw.cmd test
```

The project does not have a `lint` script in `frontend/package.json` yet.

## License

This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for more details.
