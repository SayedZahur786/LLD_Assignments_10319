package com.example.movieticket.domain;

import java.time.Instant;
import java.util.*;

public class SeatLock {
  private final String lockId;
  private final String showId;
  private final String userId;
  private final List<String> seatIds;
  private final Instant lockedAt;
  private final Instant expiresAt;
  private final long totalPriceCents;

  public SeatLock(
      String lockId,
      String showId,
      String userId,
      List<String> seatIds,
      Instant lockedAt,
      Instant expiresAt,
      long totalPriceCents) {
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("userId");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (lockedAt == null) {
      throw new IllegalArgumentException("lockedAt");
    }
    if (expiresAt == null) {
      throw new IllegalArgumentException("expiresAt");
    }
    if (totalPriceCents < 0) {
      throw new IllegalArgumentException("totalPriceCents");
    }

    this.lockId = lockId;
    this.showId = showId;
    this.userId = userId;
    this.seatIds = List.copyOf(seatIds);
    this.lockedAt = lockedAt;
    this.expiresAt = expiresAt;
    this.totalPriceCents = totalPriceCents;
  }

  public String getLockId() {
    return lockId;
  }

  public String getShowId() {
    return showId;
  }

  public String getUserId() {
    return userId;
  }

  public List<String> getSeatIds() {
    return seatIds;
  }

  public Instant getLockedAt() {
    return lockedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public long getTotalPriceCents() {
    return totalPriceCents;
  }

  @Override
  public String toString() {
    return "SeatLock{lockId='" + lockId + "', showId='" + showId + "', seats=" + seatIds + "}";
  }
}

