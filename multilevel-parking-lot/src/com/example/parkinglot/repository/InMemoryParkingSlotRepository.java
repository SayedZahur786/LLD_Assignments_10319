package com.example.parkinglot.repository;

import com.example.parkinglot.domain.ParkingSlot;
import java.util.*;

public class InMemoryParkingSlotRepository implements ParkingSlotRepository {
  private final Map<String, ParkingSlot> slotsById;

  public InMemoryParkingSlotRepository(Map<String, ParkingSlot> slotsById) {
    if (slotsById == null || slotsById.isEmpty()) {
      throw new IllegalArgumentException("slotsById");
    }
    this.slotsById = new HashMap<>(slotsById);
  }

  @Override
  public ParkingSlot findById(String id) {
    return slotsById.get(id);
  }

  @Override
  public Collection<ParkingSlot> findAll() {
    return Collections.unmodifiableCollection(slotsById.values());
  }
}
