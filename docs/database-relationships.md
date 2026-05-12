# Database relationship map

This document maps the current domain relationships before migrating the project
from manual `Long id` references to real database foreign keys and JPA
relationships.

## Current state

The application currently has no JPA relationships such as `@ManyToOne`,
`@OneToMany`, or `@JoinColumn`.

The domain is connected through plain `Long` fields:

- `booking.tee_time_id`
- `booking.created_by`
- `booking_player.booking_id`
- `booking_player.player_id`
- `rental_transaction.booking_id`
- `rental_transaction.booking_player_id`
- `rental_transaction.rental_item_id`
- `payment.booking_id`
- `payment.booking_player_id`
- `receipt.booking_id`
- `receipt.booking_player_id`
- `receipt.payment_id`
- `receipt_item.receipt_id`

The services already validate most of these relationships manually by loading
the referenced entity from its repository and throwing a business or not-found
exception when the reference is invalid.

## DTO strategy

DTOs should continue exposing IDs to the frontend.

This keeps the React frontend and REST API payloads simple:

```json
{
  "bookingId": 1,
  "playerId": 10
}
```

The internal entity model can evolve to JPA relationships, while the DTOs remain
stable and friendly for API consumers.

## Relationship matrix

| Source entity | Current field | Target entity | Planned database FK | DTO keeps ID? | Notes |
| --- | --- | --- | --- | --- | --- |
| `Booking` | `teeTimeId` | `TeeTime` | `booking.tee_time_id -> tee_time.id` | Yes, `teeTimeId` | A booking belongs to one tee time. |
| `Booking` | `createdBy` | Future `User` | `booking.created_by -> user.id` | Yes, `createdBy` | Do later, after user/security exists. Nullable for now. |
| `BookingPlayer` | `bookingId` | `Booking` | `booking_player.booking_id -> booking.id` | Yes, `bookingId` | A booking can have many booking players. |
| `BookingPlayer` | `playerId` | `Player` | `booking_player.player_id -> player.id` | Yes, `playerId` | A player can appear in many bookings. |
| `RentalTransaction` | `bookingId` | `Booking` | `rental_transaction.booking_id -> booking.id` | Yes, `bookingId` | Used for booking totals and listing rentals by booking. |
| `RentalTransaction` | `bookingPlayerId` | `BookingPlayer` | `rental_transaction.booking_player_id -> booking_player.id` | Yes, `bookingPlayerId` | Assigns the rental to one player inside the booking. |
| `RentalTransaction` | `rentalItemId` | `RentalItem` | `rental_transaction.rental_item_id -> rental_item.id` | Yes, `rentalItemId` | Used for stock and price calculation. |
| `Payment` | `bookingId` | `Booking` | `payment.booking_id -> booking.id` | Yes, `bookingId` | Payment belongs to a booking. |
| `Payment` | `bookingPlayerId` | `BookingPlayer` | `payment.booking_player_id -> booking_player.id` | Yes, `bookingPlayerId` | Payment is made per player/account line. |
| `Receipt` | `bookingId` | `Booking` | `receipt.booking_id -> booking.id` | Yes, `bookingId` | Keeps lookup by booking. |
| `Receipt` | `bookingPlayerId` | `BookingPlayer` | `receipt.booking_player_id -> booking_player.id` | Yes, `bookingPlayerId` | Receipt is issued for one player's payment. |
| `Receipt` | `paymentId` | `Payment` | `receipt.payment_id -> payment.id` | Yes, `paymentId` | Receipt is generated from a payment. |
| `ReceiptItem` | `receiptId` | `Receipt` | `receipt_item.receipt_id -> receipt.id` | Yes, `receiptId` | One receipt has many receipt items. |

## JPA relationship direction

Start with `@ManyToOne(fetch = FetchType.LAZY)` on child entities. Avoid adding
large bidirectional collections at first because they can create serialization,
performance, and cascade problems.

Recommended first mappings:

```text
Booking many-to-one TeeTime
BookingPlayer many-to-one Booking
BookingPlayer many-to-one Player
RentalTransaction many-to-one Booking
RentalTransaction many-to-one BookingPlayer
RentalTransaction many-to-one RentalItem
Payment many-to-one Booking
Payment many-to-one BookingPlayer
Receipt many-to-one Booking
Receipt many-to-one BookingPlayer
Receipt many-to-one Payment
ReceiptItem many-to-one Receipt
```

Optional later mappings:

```text
TeeTime one-to-many Booking
Booking one-to-many BookingPlayer
Booking one-to-many RentalTransaction
Booking one-to-many Payment
Booking one-to-many Receipt
Player one-to-many BookingPlayer
RentalItem one-to-many RentalTransaction
Receipt one-to-many ReceiptItem
```

## Migration order

Use small Flyway migrations and validate after each group.

1. `booking_player.booking_id -> booking.id`
2. `booking_player.player_id -> player.id`
3. `booking.tee_time_id -> tee_time.id`
4. `rental_transaction.booking_id -> booking.id`
5. `rental_transaction.booking_player_id -> booking_player.id`
6. `rental_transaction.rental_item_id -> rental_item.id`
7. `payment.booking_id -> booking.id`
8. `payment.booking_player_id -> booking_player.id`
9. `receipt.booking_id -> booking.id`
10. `receipt.booking_player_id -> booking_player.id`
11. `receipt.payment_id -> payment.id`
12. `receipt_item.receipt_id -> receipt.id`
13. Later: `booking.created_by -> user.id`

## Delete and cascade policy

Be conservative with cascade.

Recommended behavior:

- Do not cascade delete from `Booking` to `Player`.
- Do not cascade delete from `Player` to booking history.
- Do not physically delete `Receipt` in normal business flow; cancel it.
- Do not physically delete `Payment` if it already has financial history in a
  production-grade version; prefer refund/cancel status.
- Keep `RentalItem` historical rows stable. Prefer `active = false` over delete
  when the item was used in rental transactions.
- `Receipt -> ReceiptItem` is the only relationship where controlled child
  delete can make sense, but active receipts should still be protected.

## Existing service validations to preserve

When entities move to JPA relationships, these business validations must remain:

- A `BookingPlayer` must point to an existing booking and player.
- A `BookingPlayer` cannot exceed the tee time capacity.
- `tee_time.booked_players` and `tee_time.status` are updated from booking
  player changes.
- `booking.total_amount` is recalculated from green fees and active rentals.
- A `RentalTransaction` booking player must belong to the same booking.
- Rental stock must be reserved and returned correctly.
- Rental price rules for twilight and members must stay in the service layer.
- A `Payment` booking player must belong to the same booking.
- Payment cannot exceed the selected booking player's pending balance.
- A `Receipt` is issued from a paid payment and keeps historical snapshots.
- A `ReceiptItem` must belong to an existing receipt.
- Cancelled receipts cannot be changed.

## Flyway and Hibernate target

Current configuration uses:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

The professional target is:

```yaml
spring.jpa.hibernate.ddl-auto: validate
```

The migration should be gradual:

1. Add foreign keys with Flyway while keeping `ddl-auto: update`.
2. Convert Java entities one group at a time.
3. Validate all flows after each group.
4. Add indexes and final constraints.
5. Switch to `ddl-auto: validate` only when Flyway fully owns the schema.

