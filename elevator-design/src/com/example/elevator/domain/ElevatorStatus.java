package com.example.elevator.domain;

import java.util.*;

public class ElevatorStatus {
  private final String elevatorId;
  private final ElevatorState state;
  private final int currentFloor;
  private final boolean doorOpen;
  private final int currentLoadKg;
  private final int capacityKg;
  private final Set<Integer> pendingStops;
  private final boolean overweight;
  private final boolean express;

  public ElevatorStatus(
      String elevatorId,
      ElevatorState state,
      int currentFloor,
      boolean doorOpen,
      int currentLoadKg,
      int capacityKg,
      Set<Integer> pendingStops,
      boolean overweight,
      boolean express) {
    this.elevatorId = elevatorId;
    this.state = state;
    this.currentFloor = currentFloor;
    this.doorOpen = doorOpen;
    this.currentLoadKg = currentLoadKg;
    this.capacityKg = capacityKg;
    this.pendingStops = pendingStops;
    this.overweight = overweight;
    this.express = express;
  }

  public String getElevatorId() {
    return elevatorId;
  }

  public ElevatorState getState() {
    return state;
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public boolean isDoorOpen() {
    return doorOpen;
  }

  public int getCurrentLoadKg() {
    return currentLoadKg;
  }

  public int getCapacityKg() {
    return capacityKg;
  }

  public Set<Integer> getPendingStops() {
    return pendingStops;
  }

  public boolean isOverweight() {
    return overweight;
  }

  public boolean isExpress() {
    return express;
  }
}
