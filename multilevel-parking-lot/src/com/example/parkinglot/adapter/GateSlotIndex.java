package com.example.parkinglot.adapter;

import java.util.*;

public class GateSlotIndex {
  private final Map<String, List<String>> orderedSlotIdsByGate;

  public GateSlotIndex(Map<String, List<String>> orderedSlotIdsByGate) {
    if (orderedSlotIdsByGate == null || orderedSlotIdsByGate.isEmpty()) {
      throw new IllegalArgumentException("orderedSlotIdsByGate cannot be empty");
    }
    this.orderedSlotIdsByGate = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : orderedSlotIdsByGate.entrySet()) {
      String gateId = entry.getKey();
      List<String> slotIds = entry.getValue();
      if (gateId == null || gateId.isBlank() || slotIds == null) {
        throw new IllegalArgumentException("invalid gate-slot index entry");
      }
      this.orderedSlotIdsByGate.put(gateId, List.copyOf(slotIds));
    }
  }

  public List<String> getOrderedSlotIds(String gateId) {
    List<String> ids = orderedSlotIdsByGate.get(gateId);
    if (ids == null) {
      throw new IllegalArgumentException("Unknown gate: " + gateId);
    }
    return Collections.unmodifiableList(ids);
  }
}
