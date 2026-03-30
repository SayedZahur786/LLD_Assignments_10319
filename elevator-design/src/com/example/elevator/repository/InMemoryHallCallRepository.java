package com.example.elevator.repository;

import com.example.elevator.domain.HallCall;
import java.util.*;

public class InMemoryHallCallRepository implements HallCallRepository {
  private final Map<String, HallCall> hallCallsById = new HashMap<>();

  @Override
  public HallCall save(HallCall hallCall) {
    if (hallCall == null) {
      throw new IllegalArgumentException("hallCall");
    }
    synchronized (this) {
      hallCallsById.put(hallCall.getHallCallId(), hallCall);
    }
    return hallCall;
  }

  @Override
  public HallCall findById(String hallCallId) {
    if (hallCallId == null) {
      throw new IllegalArgumentException("hallCallId");
    }
    synchronized (this) {
      return hallCallsById.get(hallCallId);
    }
  }

  @Override
  public List<HallCall> findAll() {
    synchronized (this) {
      return new ArrayList<>(hallCallsById.values());
    }
  }

  @Override
  public void assignToElevator(String hallCallId, String elevatorId) {
    synchronized (this) {
      HallCall call = hallCallsById.get(hallCallId);
      if (call == null) {
        throw new IllegalStateException("hallCall not found: " + hallCallId);
      }
      if (elevatorId == null || elevatorId.isBlank()) {
        throw new IllegalArgumentException("elevatorId");
      }
      call.assignTo(elevatorId);
    }
  }

  @Override
  public List<HallCall> findUnservedAssignedToElevatorAtFloor(String elevatorId, int floor) {
    synchronized (this) {
      List<HallCall> result = new ArrayList<>();
      for (HallCall call : hallCallsById.values()) {
        if (!call.isServed()
            && elevatorId != null
            && elevatorId.equals(call.getAssignedElevatorId())
            && call.getFloor() == floor) {
          result.add(call);
        }
      }
      return result;
    }
  }

  @Override
  public List<HallCall> findUnservedAssignedToElevator(String elevatorId) {
    synchronized (this) {
      List<HallCall> result = new ArrayList<>();
      for (HallCall call : hallCallsById.values()) {
        if (!call.isServed()
            && elevatorId != null
            && elevatorId.equals(call.getAssignedElevatorId())) {
          result.add(call);
        }
      }
      return result;
    }
  }

  @Override
  public void markServed(String hallCallId) {
    synchronized (this) {
      HallCall call = hallCallsById.get(hallCallId);
      if (call == null) {
        throw new IllegalStateException("hallCall not found: " + hallCallId);
      }
      call.setServed(true);
    }
  }
}
