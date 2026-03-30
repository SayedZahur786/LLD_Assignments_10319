package com.example.parkinglot.domain;

import java.util.*;

public final class VehicleSlotCompatibility {
  private VehicleSlotCompatibility() {}

  public static List<SlotType> allowedSlotTypesFor(VehicleType vehicleType) {
    if (vehicleType == null) {
      throw new IllegalArgumentException("vehicleType");
    }
    return switch (vehicleType) {
      case TWO_WHEELER -> List.of(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE);
      case CAR -> List.of(SlotType.MEDIUM, SlotType.LARGE);
      case BUS -> List.of(SlotType.LARGE);
    };
  }

  public static List<SlotType> candidateTypes(VehicleType vehicleType, SlotType requestedSlotType) {
    List<SlotType> allowed = allowedSlotTypesFor(vehicleType);
    SlotType minType = requestedSlotType == null ? allowed.get(0) : requestedSlotType;
    List<SlotType> candidates = new ArrayList<>();
    for (SlotType slotType : allowed) {
      if (slotType.isAtLeast(minType)) {
        candidates.add(slotType);
      }
    }
    return candidates;
  }
}
