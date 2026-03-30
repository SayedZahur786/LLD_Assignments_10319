package com.example.movieticket.domain;

import java.time.Instant;

public class SeatStateRecord {
  private SeatState state;
  private String lockId;
  private Instant lockedUntil;
  private String bookingId;

  public SeatStateRecord(SeatState state) {
    if (state == null) {
      throw new IllegalArgumentException("state");
    }
    this.state = state;
  }

  public SeatState getState() {
    return state;
  }

  public String getLockId() {
    return lockId;
  }

  public Instant getLockedUntil() {
    return lockedUntil;
  }

  public String getBookingId() {
    return bookingId;
  }

  public void setAvailable() {
    this.state = SeatState.AVAILABLE;
    this.lockId = null;
    this.lockedUntil = null;
    this.bookingId = null;
  }

  public void setLocked(String lockId, Instant lockedUntil) {
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    if (lockedUntil == null) {
      throw new IllegalArgumentException("lockedUntil");
    }
    this.state = SeatState.LOCKED;
    this.lockId = lockId;
    this.lockedUntil = lockedUntil;
    this.bookingId = null;
  }

  public void setBooked(String bookingId) {
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    this.state = SeatState.BOOKED;
    this.bookingId = bookingId;
    this.lockId = null;
    this.lockedUntil = null;
  }
}

