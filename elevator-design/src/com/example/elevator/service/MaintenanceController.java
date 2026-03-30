package com.example.elevator.service;

import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorDispatchStrategy;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.ExpressStopPolicy;
import com.example.elevator.domain.HallCall;
import com.example.elevator.repository.ElevatorCarRepository;
import com.example.elevator.repository.HallCallRepository;
import java.util.*;

public class MaintenanceController {
  private final Object hallDispatchLock;
  private final Map<String, Object> elevatorLocks;
  private final ElevatorCarRepository elevatorCarRepository;
  private final HallCallRepository hallCallRepository;
  private final ElevatorDispatchStrategy dispatchStrategy;
  private final ExpressStopPolicy expressStopPolicy;

  public MaintenanceController(
      Object hallDispatchLock,
      Map<String, Object> elevatorLocks,
      ElevatorCarRepository elevatorCarRepository,
      HallCallRepository hallCallRepository,
      ElevatorDispatchStrategy dispatchStrategy,
      ExpressStopPolicy expressStopPolicy) {
    if (hallDispatchLock == null
        || elevatorLocks == null
        || elevatorCarRepository == null
        || hallCallRepository == null
        || dispatchStrategy == null
        || expressStopPolicy == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }
    this.hallDispatchLock = hallDispatchLock;
    this.elevatorLocks = elevatorLocks;
    this.elevatorCarRepository = elevatorCarRepository;
    this.hallCallRepository = hallCallRepository;
    this.dispatchStrategy = dispatchStrategy;
    this.expressStopPolicy = expressStopPolicy;
  }

  public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
    if (elevatorId == null || elevatorId.isBlank()) {
      throw new IllegalArgumentException("elevatorId");
    }

    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) throw new IllegalStateException("Unknown elevator: " + elevatorId);

    if (maintenance) {
      synchronized (hallDispatchLock) {
        synchronized (lock) {
          ElevatorCar car = elevatorCarRepository.findById(elevatorId);
          if (car == null) throw new IllegalStateException("Unknown elevator: " + elevatorId);
          car.setState(ElevatorState.UNDER_MAINTENANCE);
          car.setDoorOpen(true);
          car.clearStopFloors();
        }

        List<HallCall> calls = hallCallRepository.findUnservedAssignedToElevator(elevatorId);
        if (!calls.isEmpty()) {
          for (HallCall call : calls) {
            List<ElevatorCar> candidates = new ArrayList<>(elevatorCarRepository.findAll());
            candidates.removeIf(c -> c.getState() == ElevatorState.UNDER_MAINTENANCE);

            String newElevatorId = dispatchStrategy.assignElevator(call, candidates, expressStopPolicy);
            if (newElevatorId != null && !newElevatorId.equals(elevatorId)) {
              hallCallRepository.assignToElevator(call.getHallCallId(), newElevatorId);

              Object candidateLock = elevatorLocks.get(newElevatorId);
              if (candidateLock != null) {
                synchronized (candidateLock) {
                  ElevatorCar candidate = elevatorCarRepository.findById(newElevatorId);
                  if (candidate != null) candidate.addStopFloor(call.getFloor());
                }
              }
            }
          }
        }
      }
      return;
    }

    synchronized (lock) {
      ElevatorCar car = elevatorCarRepository.findById(elevatorId);
      if (car == null) throw new IllegalStateException("Unknown elevator: " + elevatorId);
      car.setState(ElevatorState.IDLE);
      car.setDoorOpen(false);
    }
  }
}

