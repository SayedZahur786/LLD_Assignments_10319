package com.example.elevator.domain;

import java.util.*;

public class ElevatorCarSpec {
  private final String elevatorId;
  private final int capacityKg;
  private final boolean expressEnabled;
  private final Set<Integer> expressAllowedStops;

  public ElevatorCarSpec(
      String elevatorId, int capacityKg, boolean expressEnabled, Set<Integer> expressAllowedStops) {
    if (elevatorId == null || elevatorId.isBlank()) {
      throw new IllegalArgumentException("elevatorId");
    }
    if (capacityKg <= 0) {
      throw new IllegalArgumentException("capacityKg must be > 0");
    }
    this.elevatorId = elevatorId;
    this.capacityKg = capacityKg;
    this.expressEnabled = expressEnabled;
    if (expressAllowedStops == null) {
      this.expressAllowedStops = new HashSet<>();
    } else {
      this.expressAllowedStops = new HashSet<>(expressAllowedStops);
    }
  }

  public String getElevatorId() {
    return elevatorId;
  }

  public int getCapacityKg() {
    return capacityKg;
  }

  public boolean isExpressEnabled() {
    return expressEnabled;
  }

  public Set<Integer> getExpressAllowedStops() {
    return expressAllowedStops;
  }

  public boolean canStopAtFloor(int floor, ExpressStopPolicy expressStopPolicy) {
    if (expressStopPolicy == null) {
      throw new IllegalArgumentException("expressStopPolicy");
    }
    return expressStopPolicy.canStopAt(this, floor);
  }
}
