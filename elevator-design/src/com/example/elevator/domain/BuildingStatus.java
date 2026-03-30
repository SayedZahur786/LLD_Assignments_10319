package com.example.elevator.domain;

import java.util.*;

public class BuildingStatus {
  private final Set<Integer> enabledFloors;
  private final Set<Integer> disabledFloors;
  private final boolean emergencyActive;
  private final EmergencyType emergencyType;
  private final List<ElevatorStatus> elevators;

  public BuildingStatus(
      Set<Integer> enabledFloors,
      Set<Integer> disabledFloors,
      boolean emergencyActive,
      EmergencyType emergencyType,
      List<ElevatorStatus> elevators) {
    this.enabledFloors = enabledFloors;
    this.disabledFloors = disabledFloors;
    this.emergencyActive = emergencyActive;
    this.emergencyType = emergencyType;
    this.elevators = elevators;
  }

  public Set<Integer> getEnabledFloors() {
    return enabledFloors;
  }

  public Set<Integer> getDisabledFloors() {
    return disabledFloors;
  }

  public boolean isEmergencyActive() {
    return emergencyActive;
  }

  public EmergencyType getEmergencyType() {
    return emergencyType;
  }

  public List<ElevatorStatus> getElevators() {
    return elevators;
  }
}
