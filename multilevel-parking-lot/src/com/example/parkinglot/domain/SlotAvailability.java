package com.example.parkinglot.domain;

import java.util.*;

public class SlotAvailability {
  private final Map<SlotType, Integer> freeCounts;

  public SlotAvailability(Map<SlotType, Integer> freeCounts) {
    if (freeCounts == null) {
      throw new IllegalArgumentException("freeCounts");
    }
    this.freeCounts = new EnumMap<>(SlotType.class);
    for (SlotType slotType : SlotType.values()) {
      this.freeCounts.put(slotType, freeCounts.getOrDefault(slotType, 0));
    }
  }

  public int getFreeCount(SlotType slotType) {
    return freeCounts.getOrDefault(slotType, 0);
  }

  public Map<SlotType, Integer> asMap() {
    return Collections.unmodifiableMap(freeCounts);
  }

  @Override
  public String toString() {
    return String.valueOf(freeCounts);
  }
}
