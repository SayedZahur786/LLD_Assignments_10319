package com.example.parkinglot.domain;

import java.util.*;

public class RateCard {
  private final Map<SlotType, Integer> hourlyRateCents;

  public RateCard(Map<SlotType, Integer> hourlyRateCents) {
    if (hourlyRateCents == null) {
      throw new IllegalArgumentException("hourlyRateCents");
    }
    this.hourlyRateCents = new EnumMap<>(SlotType.class);
    for (SlotType slotType : SlotType.values()) {
      Integer rate = hourlyRateCents.get(slotType);
      if (rate == null || rate < 0) {
        throw new IllegalArgumentException("missing/invalid rate for " + slotType);
      }
      this.hourlyRateCents.put(slotType, rate);
    }
  }

  public int rateFor(SlotType slotType) {
    Integer rate = hourlyRateCents.get(slotType);
    if (rate == null) {
      throw new IllegalArgumentException("rate not found for " + slotType);
    }
    return rate;
  }
}
