package com.example.movieticket.domain;

import java.time.Instant;
import java.util.*;

public class SeatView {
  private final String seatId;
  private final SeatCategory category;
  private final SeatState state;
  private final Instant lockedUntil;

  public SeatView(String seatId, SeatCategory category, SeatState state, Instant lockedUntil) {
    if (seatId == null || seatId.isBlank()) {
      throw new IllegalArgumentException("seatId");
    }
    if (category == null) {
      throw new IllegalArgumentException("category");
    }
    if (state == null) {
      throw new IllegalArgumentException("state");
    }
    this.seatId = seatId;
    this.category = category;
    this.state = state;
    this.lockedUntil = lockedUntil;
  }

  public String getSeatId() {
    return seatId;
  }

  public SeatCategory getCategory() {
    return category;
  }

  public SeatState getState() {
    return state;
  }

  public Instant getLockedUntil() {
    return lockedUntil;
  }

  @Override
  public String toString() {
    return "SeatView{"
        + "seatId="
        + seatId
        + ", category="
        + category
        + ", state="
        + state
        + ", lockedUntil="
        + lockedUntil
        + '}';
  }
}

