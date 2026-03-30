package com.example.elevator.domain;

import java.util.*;

public class ElevatorCar {
  private final ElevatorCarSpec spec;

  private volatile int currentFloor;
  private volatile ElevatorState state;
  private volatile boolean doorOpen;
  private volatile int currentLoadKg;

  private final Set<Integer> stopFloors = new TreeSet<>();
  private volatile boolean overweightAlarmActive;

  public ElevatorCar(ElevatorCarSpec spec, int startingFloor) {
    if (spec == null) {
      throw new IllegalArgumentException("spec");
    }
    if (startingFloor < 0) {
      throw new IllegalArgumentException("startingFloor");
    }
    this.spec = spec;
    this.currentFloor = startingFloor;
    this.state = ElevatorState.IDLE;
    this.doorOpen = false;
    this.currentLoadKg = 0;
    this.overweightAlarmActive = false;
  }

  public ElevatorCarSpec getSpec() {
    return spec;
  }

  public String getElevatorId() {
    return spec.getElevatorId();
  }

  public int getCurrentFloor() {
    return currentFloor;
  }

  public ElevatorState getState() {
    return state;
  }

  public boolean isDoorOpen() {
    return doorOpen;
  }

  public int getCurrentLoadKg() {
    return currentLoadKg;
  }

  public boolean isOverweight() {
    return currentLoadKg > spec.getCapacityKg();
  }

  public boolean isOverweightAlarmActive() {
    return overweightAlarmActive;
  }

  public void setOverweightAlarmActive(boolean overweightAlarmActive) {
    this.overweightAlarmActive = overweightAlarmActive;
  }

  public void setState(ElevatorState state) {
    if (state == null) {
      throw new IllegalArgumentException("state");
    }
    this.state = state;
  }

  public void setDoorOpen(boolean doorOpen) {
    this.doorOpen = doorOpen;
  }

  public void setCurrentFloor(int currentFloor) {
    this.currentFloor = currentFloor;
  }

  public void setCurrentLoadKg(int currentLoadKg) {
    if (currentLoadKg < 0) {
      throw new IllegalArgumentException("currentLoadKg");
    }
    this.currentLoadKg = currentLoadKg;
  }

  public void addStopFloor(int floor) {
    stopFloors.add(floor);
  }

  public void removeStopFloor(int floor) {
    stopFloors.remove(floor);
  }

  public void clearStopFloors() {
    stopFloors.clear();
  }

  public boolean hasStopFloor(int floor) {
    return stopFloors.contains(floor);
  }

  public boolean isStopFloorsEmpty() {
    return stopFloors.isEmpty();
  }

  public Integer nextStopAbove(int floor) {
    for (Integer stop : stopFloors) {
      if (stop > floor) return stop;
    }
    return null;
  }

  public Integer nextStopBelow(int floor) {
    Integer last = null;
    for (Integer stop : stopFloors) {
      if (stop < floor) last = stop;
    }
    return last;
  }

  public Set<Integer> stopFloorsSnapshot() {
    if (stopFloors.isEmpty()) {
      return new HashSet<>();
    }
    return new TreeSet<>(stopFloors);
  }
}
