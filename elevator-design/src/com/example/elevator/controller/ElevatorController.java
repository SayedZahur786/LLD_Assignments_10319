package com.example.elevator.controller;

import com.example.elevator.domain.BuildingStatus;
import com.example.elevator.domain.CallPriority;
import com.example.elevator.domain.Direction;
import com.example.elevator.domain.DispatchResult;
import com.example.elevator.domain.ElevatorCarSpec;
import com.example.elevator.domain.EmergencyType;
import com.example.elevator.service.ElevatorSystemService;
import java.util.*;

public class ElevatorController {
  private final ElevatorSystemService elevatorSystemService;

  public ElevatorController(ElevatorSystemService elevatorSystemService) {
    if (elevatorSystemService == null) {
      throw new IllegalArgumentException("elevatorSystemService");
    }
    this.elevatorSystemService = elevatorSystemService;
  }

  public DispatchResult pressOutsideHallButton(int floor, Direction direction, Locale locale) {
    return elevatorSystemService.pressHallButton(floor, direction, locale);
  }

  public DispatchResult pressOutsideHallButton(
      int floor, Direction direction, CallPriority priority, Locale locale) {
    return elevatorSystemService.pressHallButton(floor, direction, priority, locale);
  }

  public boolean pressInCarButton(String elevatorId, int floor, Locale locale) {
    return elevatorSystemService.pressInCarButton(elevatorId, floor, locale);
  }

  public boolean pressDoorOpenButton(String elevatorId) {
    return elevatorSystemService.openDoor(elevatorId, Locale.ENGLISH);
  }

  public boolean pressDoorCloseButton(String elevatorId) {
    return elevatorSystemService.closeDoor(elevatorId);
  }

  public boolean updateElevatorLoadKg(String elevatorId, int loadKg, Locale locale) {
    return elevatorSystemService.updateElevatorLoadKg(elevatorId, loadKg, locale);
  }

  public void triggerEmergency(EmergencyType type, Locale locale) {
    elevatorSystemService.triggerEmergency(type, locale);
  }

  public void clearEmergency() {
    elevatorSystemService.clearEmergency();
  }

  public void addFloor(int floor) {
    elevatorSystemService.addFloor(floor);
  }

  public void setFloorEnabled(int floor, boolean enabled) {
    elevatorSystemService.setFloorEnabled(floor, enabled);
  }

  public void setPriorityFloor(int floor, boolean prioritized) {
    elevatorSystemService.setPriorityFloor(floor, prioritized);
  }

  public void setHallButtonEnabled(int floor, Direction direction, boolean enabled) {
    elevatorSystemService.setHallButtonEnabled(floor, direction, enabled);
  }

  public void disableInCarButton(String elevatorId, int floor, boolean disabled) {
    elevatorSystemService.disableInCarButton(elevatorId, floor, disabled);
  }

  public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
    elevatorSystemService.setElevatorMaintenance(elevatorId, maintenance);
  }

  public void addElevatorCar(ElevatorCarSpec spec, int startingFloor) {
    elevatorSystemService.addElevatorCar(spec, startingFloor);
  }

  public void tickAllElevators() {
    elevatorSystemService.tickAllElevators();
  }

  public void tickElevator(String elevatorId) {
    elevatorSystemService.tickElevator(elevatorId);
  }

  public BuildingStatus status() {
    return elevatorSystemService.status();
  }
}
