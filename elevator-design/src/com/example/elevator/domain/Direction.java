package com.example.elevator.domain;

public enum Direction {
  UP,
  DOWN;

  public Direction opposite() {
    return this == UP ? DOWN : UP;
  }
}
