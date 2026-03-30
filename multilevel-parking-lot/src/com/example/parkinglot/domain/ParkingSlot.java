package com.example.parkinglot.domain;

public class ParkingSlot {
  private final String id;
  private final SlotType type;
  private final int floor;
  private boolean occupied;

  public ParkingSlot(String id, SlotType type, int floor) {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("slot id");
    }
    if (type == null) {
      throw new IllegalArgumentException("slot type");
    }
    this.id = id;
    this.type = type;
    this.floor = floor;
    this.occupied = false;
  }

  public String getId() {
    return id;
  }

  public SlotType getType() {
    return type;
  }

  public int getFloor() {
    return floor;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public void occupy() {
    if (occupied) {
      throw new IllegalStateException("already occupied: " + id);
    }
    occupied = true;
  }

  public void release() {
    if (!occupied) {
      throw new IllegalStateException("already empty: " + id);
    }
    occupied = false;
  }
}
