package com.example.parkinglot.service;

import com.example.parkinglot.domain.Bill;
import com.example.parkinglot.domain.ParkingSlot;
import com.example.parkinglot.domain.ParkingTicket;
import com.example.parkinglot.domain.RateCard;
import com.example.parkinglot.domain.SlotAssignmentStrategy;
import com.example.parkinglot.domain.SlotAvailability;
import com.example.parkinglot.domain.SlotType;
import com.example.parkinglot.domain.Vehicle;
import com.example.parkinglot.repository.ParkingSlotRepository;
import com.example.parkinglot.repository.ParkingTicketRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class ParkingLotService {
  private final ParkingSlotRepository slotRepository;
  private final ParkingTicketRepository ticketRepository;
  private final RateCard rateCard;
  private final SlotAssignmentStrategy slotAssignmentStrategy;

  public ParkingLotService(
      ParkingSlotRepository slotRepository,
      ParkingTicketRepository ticketRepository,
      RateCard rateCard,
      SlotAssignmentStrategy slotAssignmentStrategy) {
    if (slotRepository == null
        || ticketRepository == null
        || rateCard == null
        || slotAssignmentStrategy == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }
    this.slotRepository = slotRepository;
    this.ticketRepository = ticketRepository;
    this.rateCard = rateCard;
    this.slotAssignmentStrategy = slotAssignmentStrategy;
  }

  public ParkingTicket park(
      Vehicle vehicleDetails, Instant entryTime, SlotType requestedSlotType, String entryGateID) {
    if (entryTime == null) {
      throw new IllegalArgumentException("entryTime");
    }
    ParkingSlot allocated =
        slotAssignmentStrategy.assign(vehicleDetails, requestedSlotType, entryGateID);
    if (allocated == null) {
      throw new IllegalStateException("No compatible slot available for gate: " + entryGateID);
    }
    allocated.occupy();
    String ticketId = UUID.randomUUID().toString();
    ParkingTicket ticket =
        new ParkingTicket(
            ticketId,
            vehicleDetails,
            allocated.getId(),
            allocated.getType(),
            entryGateID,
            entryTime);
    ticketRepository.save(ticket);
    return ticket;
  }

  public SlotAvailability status() {
    Map<SlotType, Integer> freeCounts = new EnumMap<>(SlotType.class);
    for (SlotType slotType : SlotType.values()) {
      freeCounts.put(slotType, 0);
    }
    for (ParkingSlot slot : slotRepository.findAll()) {
      if (!slot.isOccupied()) {
        freeCounts.put(slot.getType(), freeCounts.get(slot.getType()) + 1);
      }
    }
    return new SlotAvailability(freeCounts);
  }

  public Bill exit(ParkingTicket parkingTicket, Instant exitTime) {
    if (parkingTicket == null || exitTime == null) {
      throw new IllegalArgumentException("ticket and exitTime required");
    }

    ParkingTicket active = ticketRepository.removeActive(parkingTicket.getTicketId());

    ParkingSlot slot = slotRepository.findById(active.getSlotId());
    if (slot == null) {
      throw new IllegalStateException("Allocated slot not found: " + active.getSlotId());
    }

    Duration parkedDuration = Duration.between(active.getEntryTime(), exitTime);
    if (parkedDuration.isNegative()) {
      throw new IllegalArgumentException("exit before entry");
    }

    long parkedMinutes = parkedDuration.toMinutes();
    long billableHours = Math.max(1, (long) Math.ceil(parkedMinutes / 60.0));
    long amountCents = billableHours * rateCard.rateFor(active.getAllocatedSlotType());

    slot.release();
    return new Bill(active.getTicketId(), parkedDuration, billableHours, amountCents);
  }
}
