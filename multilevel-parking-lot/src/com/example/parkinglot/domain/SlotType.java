package com.example.parkinglot.domain;

public enum SlotType {
  SMALL(1),
  MEDIUM(2),
  LARGE(3);

  private final int rank;

  SlotType(int rank) {
    this.rank = rank;
  }

  public boolean isAtLeast(SlotType other) {
    return this.rank >= other.rank;
  }
}
