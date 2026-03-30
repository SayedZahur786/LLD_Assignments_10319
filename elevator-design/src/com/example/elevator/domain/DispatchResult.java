package com.example.elevator.domain;

public class DispatchResult {
  private final String hallCallId;
  private final String elevatorId;
  private final boolean accepted;
  private final String message;

  private DispatchResult(String hallCallId, String elevatorId, boolean accepted, String message) {
    this.hallCallId = hallCallId;
    this.elevatorId = elevatorId;
    this.accepted = accepted;
    this.message = message;
  }

  public static DispatchResult accepted(String hallCallId, String elevatorId) {
    return new DispatchResult(hallCallId, elevatorId, true, null);
  }

  public static DispatchResult rejected(String hallCallId, String message) {
    return new DispatchResult(hallCallId, null, false, message);
  }

  public String getHallCallId() {
    return hallCallId;
  }

  public String getElevatorId() {
    return elevatorId;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "DispatchResult{"
        + "hallCallId='"
        + hallCallId
        + '\''
        + ", elevatorId='"
        + elevatorId
        + '\''
        + ", accepted="
        + accepted
        + (message != null ? ", message='" + message + '\'' : "")
        + '}';
  }
}
