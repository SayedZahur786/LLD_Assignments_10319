# Movie ticket booking

## Explanation of the design and approach

### What this is

- A booking system for movies, multiple theaters per city, multiple screens per theater, and multiple shows per screen.
- User can view movies, theaters, shows, then pick a show, select seats, lock seats for a short time, pay, confirm the booking, or cancel before the show starts.
- This version keeps everything in memory so the focus stays on the LLD flow, seat locking, pricing, and payment states.

### Booking flow

- User selects city, movie or theater, and then the show.
- System shows the seat map with availability.
- User selects seats, the system locks those seats for a fixed TTL.
- After payment:
  - success means LOCKED seats become BOOKED and booking status becomes CONFIRMED
  - failure means seats are released back to AVAILABLE

### Cancel flow

- User can cancel a confirmed booking before show start time.
- We release the seats back to AVAILABLE, update booking status, and trigger a refund for the payment.

### Pricing and payments

- Each seat belongs to a category like Silver, Gold, Platinum.
- Final price is calculated using base price, a time multiplier, and a demand based surge multiplier from occupancy.
- Payments go through a gateway adapter, and the payment state moves between INITIATED, SUCCESS, and FAILED.

### How the code is layered

- Controller is the front door, it forwards calls to the service.
- Service holds the rules, seat locking, payment confirmation, and cancellation.
- Repositories keep show data, seat inventory, bookings, and payments in memory.
- Adapter code includes the payment gateway and pricing helpers.

## Class diagram
<img src="https://github.com/user-attachments/assets/311933b2-5f3b-4c19-8b57-403b874de983" alt="Class diagram" width="100%" />