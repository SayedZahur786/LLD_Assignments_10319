package com.example.parkinglot.domain;

import java.time.Instant;

public class ParkingTicket {
  private final String ticketId;
  private final Vehicle vehicle;
  private final String slotId;
  private final SlotType allocatedSlotType;
  private final String entryGateId;
  private final Instant entryTime;

  public ParkingTicket(
      String ticketId,
      Vehicle vehicle,
      String slotId,
      SlotType allocatedSlotType,
      String entryGateId,
      Instant entryTime) {
    if (ticketId == null || ticketId.isBlank()) {
      throw new IllegalArgumentException("ticketId");
    }
    if (slotId == null || slotId.isBlank()) {
      throw new IllegalArgumentException("slotId");
    }
    if (entryGateId == null || entryGateId.isBlank()) {
      throw new IllegalArgumentException("entryGateId");
    }
    if (vehicle == null || allocatedSlotType == null || entryTime == null) {
      throw new IllegalArgumentException("vehicle, allocatedSlotType, entryTime");
    }
    this.ticketId = ticketId;
    this.vehicle = vehicle;
    this.slotId = slotId;
    this.allocatedSlotType = allocatedSlotType;
    this.entryGateId = entryGateId;
    this.entryTime = entryTime;
  }

  public String getTicketId() {
    return ticketId;
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

  public String getSlotId() {
    return slotId;
  }

  public SlotType getAllocatedSlotType() {
    return allocatedSlotType;
  }

  public String getEntryGateId() {
    return entryGateId;
  }

  public Instant getEntryTime() {
    return entryTime;
  }

  @Override
  public String toString() {
    return ticketId
        + " "
        + vehicle
        + " slot "
        + slotId
        + " "
        + allocatedSlotType
        + " @"
        + entryGateId
        + " "
        + entryTime;
  }
}
