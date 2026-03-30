package com.example.elevator.domain;

import java.util.*;

public class HallCall {
  private final String hallCallId;
  private final int floor;
  private final Direction direction;
  private final CallPriority priority;
  private final long sequenceNumber;

  private volatile String assignedElevatorId;
  private volatile boolean served;

  public HallCall(
      String hallCallId,
      int floor,
      Direction direction,
      CallPriority priority,
      long sequenceNumber) {
    if (hallCallId == null || hallCallId.isBlank()) {
      throw new IllegalArgumentException("hallCallId");
    }
    if (direction == null) {
      throw new IllegalArgumentException("direction");
    }
    if (priority == null) {
      throw new IllegalArgumentException("priority");
    }
    this.hallCallId = hallCallId;
    this.floor = floor;
    this.direction = direction;
    this.priority = priority;
    this.sequenceNumber = sequenceNumber;
  }

  public String getHallCallId() {
    return hallCallId;
  }

  public int getFloor() {
    return floor;
  }

  public Direction getDirection() {
    return direction;
  }

  public CallPriority getPriority() {
    return priority;
  }

  public long getSequenceNumber() {
    return sequenceNumber;
  }

  public String getAssignedElevatorId() {
    return assignedElevatorId;
  }

  public void assignTo(String elevatorId) {
    this.assignedElevatorId = elevatorId;
  }

  public boolean isServed() {
    return served;
  }

  public void setServed(boolean served) {
    this.served = served;
  }

  @Override
  public String toString() {
    return "HallCall{"
        + "hallCallId='"
        + hallCallId
        + '\''
        + ", floor="
        + floor
        + ", direction="
        + direction
        + ", priority="
        + priority
        + ", sequenceNumber="
        + sequenceNumber
        + ", assignedElevatorId='"
        + assignedElevatorId
        + '\''
        + ", served="
        + served
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HallCall)) return false;
    HallCall hallCall = (HallCall) o;
    return Objects.equals(hallCallId, hallCall.hallCallId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hallCallId);
  }
}
