package com.example.elevator.service;

import com.example.elevator.domain.AnnouncementKey;
import com.example.elevator.domain.AnnouncementService;
import com.example.elevator.domain.CallPriority;
import com.example.elevator.domain.Direction;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.HallCall;
import com.example.elevator.repository.ElevatorCarRepository;
import com.example.elevator.repository.HallCallRepository;
import java.util.*;

public class MotionSimulator {
  private final Map<String, Object> elevatorLocks;
  private final ElevatorCarRepository elevatorCarRepository;
  private final HallCallRepository hallCallRepository;
  private final AnnouncementService announcementService;

  public MotionSimulator(
      Map<String, Object> elevatorLocks,
      ElevatorCarRepository elevatorCarRepository,
      HallCallRepository hallCallRepository,
      AnnouncementService announcementService) {
    if (elevatorLocks == null
        || elevatorCarRepository == null
        || hallCallRepository == null
        || announcementService == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }
    this.elevatorLocks = elevatorLocks;
    this.elevatorCarRepository = elevatorCarRepository;
    this.hallCallRepository = hallCallRepository;
    this.announcementService = announcementService;
  }

  public void tickAllElevators(boolean emergencyActive) {
    List<ElevatorCar> elevators = elevatorCarRepository.findAll();
    for (ElevatorCar car : elevators) {
      tickElevator(car.getElevatorId(), emergencyActive);
    }
  }

  public void tickElevator(String elevatorId, boolean emergencyActive) {
    if (elevatorId == null || elevatorId.isBlank()) throw new IllegalArgumentException("elevatorId");

    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) return;

    synchronized (lock) {
      ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
      if (elevator == null) return;

      if (elevator.getState() == ElevatorState.UNDER_MAINTENANCE) return;
      if (emergencyActive) {
        elevator.setDoorOpen(true);
        elevator.setState(ElevatorState.IDLE);
        return;
      }

      if (elevator.isOverweight()) {
        elevator.setDoorOpen(true);
        elevator.setState(ElevatorState.IDLE);
        return;
      }

      if (elevator.isDoorOpen()) {
        // Door is open; movement is paused until closeDoor() is called.
        return;
      }

      int current = elevator.getCurrentFloor();
      Set<Integer> pendingStops = elevator.stopFloorsSnapshot();

      if (pendingStops.isEmpty()) {
        elevator.setState(ElevatorState.IDLE);
        return;
      }

      // If we're at a requested stop, open door and serve.
      if (elevator.hasStopFloor(current)) {
        elevator.setDoorOpen(true);
        elevator.setState(ElevatorState.IDLE);
        elevator.removeStopFloor(current);
        serveHallCallsAtFloor(elevatorId, current);
        return;
      }

      // Decide direction if currently idle.
      if (elevator.getState() == ElevatorState.IDLE) {
        Integer nextAbove = elevator.nextStopAbove(current);
        if (nextAbove != null) elevator.setState(ElevatorState.MOVING_UP);
        else elevator.setState(ElevatorState.MOVING_DOWN);
      }

      if (elevator.getState() == ElevatorState.MOVING_UP) {
        Integer nextAbove = elevator.nextStopAbove(current);
        if (nextAbove == null) {
          elevator.setState(ElevatorState.MOVING_DOWN);
          return;
        }
        elevator.setCurrentFloor(current + 1);
      } else if (elevator.getState() == ElevatorState.MOVING_DOWN) {
        Integer nextBelow = elevator.nextStopBelow(current);
        if (nextBelow == null) {
          elevator.setState(ElevatorState.MOVING_UP);
          return;
        }
        elevator.setCurrentFloor(current - 1);
      } else {
        elevator.setState(ElevatorState.IDLE);
      }
    }
  }

  private void serveHallCallsAtFloor(String elevatorId, int floor) {
    List<HallCall> toServe = hallCallRepository.findUnservedAssignedToElevatorAtFloor(elevatorId, floor);
    if (toServe.isEmpty()) return;

    for (HallCall call : toServe) {
      hallCallRepository.markServed(call.getHallCallId());

      Map<String, String> params = new HashMap<>();
      params.put("elevatorId", elevatorId);
      params.put("floor", String.valueOf(floor));
      params.put("direction", call.getDirection().toString());
      params.put("priority", call.getPriority().toString());
      announcementService.announce(AnnouncementKey.ELEVATOR_ASSIGNED, Locale.ENGLISH, params);
    }
  }
}

