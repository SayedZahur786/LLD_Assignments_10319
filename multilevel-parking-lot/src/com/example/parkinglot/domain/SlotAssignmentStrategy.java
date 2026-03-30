package com.example.parkinglot.domain;

public interface SlotAssignmentStrategy {
  ParkingSlot assign(Vehicle vehicle, SlotType requestedSlotType, String entryGateId);
}
