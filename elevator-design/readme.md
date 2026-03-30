# Elevator system

## Explanation of the design and approach

### What this is
- Multiple elevator cars in one building.
- Two kinds of buttons:
  - Outside hall buttons per floor (`up` / `down`) are routed to exactly one elevator.
  - Inside car panels request specific stop floors for that elevator.
- Supports:
  - Elevator states (`MOVING_UP`, `MOVING_DOWN`, `IDLE`, `UNDER_MAINTENANCE`)
  - Capacity/weight limit per elevator (default configurable)
  - Emergency power outage (lock movement, keep doors open)
  - Express elevators (allowlist of allowed stop floors)
  - Dynamic updates: add floors/elevators, toggle maintenance, disable buttons

### Key rules
- Energy-efficient assignment: **only one elevator responds to each hall call**.
- Weight limit exceeded:
  - Play a sound
  - Keep the door open
  - Elevator does not move until load becomes safe again
- Concurrency:
  - Hall-call dispatch is guarded by a single lock, so simultaneous button presses still result in one assignment per call.

### How the code is layered
- `controller`: front door API that forwards button presses to the service
- `service`: core business logic (dispatch, state transitions, weight/emergency rules)
- `domain`: entities + interfaces (dispatch strategy, express stop policy, status models)
- `repository`: in-memory storage for elevators and hall calls
- `adapter`: swappable algorithms + adapters for sound/announcements

## Class diagram

<img src="https://github.com/user-attachments/assets/7b14ee5c-c649-4043-9cf6-29a09ebbb127" alt="Class diagram" width="100%" />
