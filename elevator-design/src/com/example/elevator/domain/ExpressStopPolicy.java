package com.example.elevator.domain;

public interface ExpressStopPolicy {
  boolean canStopAt(ElevatorCarSpec spec, int floor);
}
