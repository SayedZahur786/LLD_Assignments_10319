# Multilevel parking lot

## Explanation of the design and approach

### What this is

- Picture a parking building with small, medium, and large spots and more than one entry gate.
- Car comes in, we give them a spot and a ticket. They leave, we charge based on how long they stayed.
- This version keeps everything in memory. No real database, no fancy threading, just enough to show how it works.

### When someone parks

- You tell us the vehicle, when they came in, what kind of spot they asked for, and which gate they used.
- We look for a free spot that fits that vehicle. Each gate has its own list of spot ids in a fixed order. We walk that list and take the first empty one that still works for that vehicle.
- “Nearest” does not mean measuring meters on a floor plan. It really means “first good empty spot in this gate’s list.”
- A small vehicle can end up in a bigger spot if that is what is free. Bikes can go anywhere, cars need at least medium, buses only fit large.
- We book the spot and write a ticket with the vehicle, which spot they got, what size that spot actually is, which gate, and the entry time. We store the spot size because that is what we use to price later.

### When someone leaves

- We find their ticket, free the spot, and figure out how long they stayed.
- The bill uses the spot type they actually parked in, not just the vehicle type. So a bike in a medium spot pays the medium price.
- We round time up to whole hours and multiply by the rate for that spot size. Rates live in a small table called RateCard.

### Checking how full the lot is

- We can count free spots per size.

### How the pieces fit together

- **Controller** is the front door. It just forwards calls.
- **Service** is where the real rules live, park, leave, money, counts.
- **Repositories** hold slots and tickets in memory today. You could plug something else in later without rewriting the whole app.
- **Strategy** picks the spot. Right now we use a simple “walk the gate list” rule, but it is behind an interface so you could swap the idea later.
- **GateSlotIndex** is a small map, for each gate, which spot ids to try and in what order.
- The rest are plain objects, vehicle, slot, ticket, bill, enums for sizes, and a helper that says which spot sizes match which vehicle.

## Class diagram

<img src="https://github.com/user-attachments/assets/83b0b4d9-05e3-4344-960a-91cc53f6c089" alt="Class diagram" width="100%" />