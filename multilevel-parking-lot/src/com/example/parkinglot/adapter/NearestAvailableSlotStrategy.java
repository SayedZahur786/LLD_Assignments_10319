package com.example.parkinglot.adapter;

import com.example.parkinglot.domain.ParkingSlot;
import com.example.parkinglot.domain.SlotAssignmentStrategy;
import com.example.parkinglot.domain.SlotType;
import com.example.parkinglot.domain.Vehicle;
import com.example.parkinglot.domain.VehicleSlotCompatibility;
import com.example.parkinglot.repository.ParkingSlotRepository;
import java.util.*;

public class NearestAvailableSlotStrategy implements SlotAssignmentStrategy {
  private final ParkingSlotRepository slotRepository;
  private final GateSlotIndex gateSlotIndex;

  public NearestAvailableSlotStrategy(
      ParkingSlotRepository slotRepository, GateSlotIndex gateSlotIndex) {
    if (slotRepository == null || gateSlotIndex == null) {
      throw new IllegalArgumentException("slotRepository and gateSlotIndex required");
    }
    this.slotRepository = slotRepository;
    this.gateSlotIndex = gateSlotIndex;
  }

  @Override
  public ParkingSlot assign(Vehicle vehicle, SlotType requestedSlotType, String entryGateId) {
    if (vehicle == null || entryGateId == null || entryGateId.isBlank()) {
      throw new IllegalArgumentException("vehicle and entryGateId");
    }

    List<SlotType> candidates =
        VehicleSlotCompatibility.candidateTypes(vehicle.getVehicleType(), requestedSlotType);
    Set<SlotType> candidateSet = new HashSet<>(candidates);

    for (String slotId : gateSlotIndex.getOrderedSlotIds(entryGateId)) {
      ParkingSlot slot = slotRepository.findById(slotId);
      if (slot == null) {
        continue;
      }
      if (!slot.isOccupied() && candidateSet.contains(slot.getType())) {
        return slot;
      }
    }
    return null;
  }
}
