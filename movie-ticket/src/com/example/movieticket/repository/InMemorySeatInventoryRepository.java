package com.example.movieticket.repository;

import com.example.movieticket.domain.*;
import java.time.Instant;
import java.util.*;

public class InMemorySeatInventoryRepository implements SeatInventoryRepository {

  private static class ShowInventory {
    private final Show show;
    private final Object lock = new Object();
    private final Map<String, SeatCategory> categoryBySeatId;
    private final Map<String, SeatStateRecord> seatStateBySeatId;

    private ShowInventory(Show show) {
      this.show = show;
      this.categoryBySeatId = new HashMap<>(show.getSeatCategoryBySeatId());
      this.seatStateBySeatId = new HashMap<>();
      for (String seatId : categoryBySeatId.keySet()) {
        seatStateBySeatId.put(seatId, new SeatStateRecord(SeatState.AVAILABLE));
      }
    }
  }

  private final Map<String, ShowInventory> inventoriesByShowId = new HashMap<>();

  @Override
  public void initializeShow(Show show) {
    if (show == null) {
      throw new IllegalArgumentException("show");
    }
    synchronized (inventoriesByShowId) {
      if (inventoriesByShowId.containsKey(show.getShowId())) {
        return;
      }
      inventoriesByShowId.put(show.getShowId(), new ShowInventory(show));
    }
  }

  private ShowInventory inventoryFor(String showId) {
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    ShowInventory inv = inventoriesByShowId.get(showId);
    if (inv == null) {
      throw new IllegalArgumentException("Unknown show: " + showId);
    }
    return inv;
  }

  @Override
  public List<SeatView> getSeatMap(String showId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);
      List<SeatView> result = new ArrayList<>();
      for (Map.Entry<String, SeatCategory> entry : inv.categoryBySeatId.entrySet()) {
        String seatId = entry.getKey();
        SeatCategory category = entry.getValue();
        SeatStateRecord record = inv.seatStateBySeatId.get(seatId);
        Instant lockedUntil = record.getLockedUntil();
        result.add(new SeatView(seatId, category, record.getState(), lockedUntil));
      }
      result.sort(Comparator.comparing(SeatView::getSeatId));
      return Collections.unmodifiableList(result);
    }
  }

  @Override
  public void lockSeats(
      String showId,
      List<String> seatIds,
      String lockId,
      String userId,
      Instant now,
      long ttlSeconds) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("userId");
    }
    if (ttlSeconds <= 0) {
      throw new IllegalArgumentException("ttlSeconds");
    }

    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);

      for (String seatId : seatIds) {
        SeatStateRecord record = inv.seatStateBySeatId.get(seatId);
        if (record == null) {
          throw new IllegalArgumentException("Unknown seatId: " + seatId);
        }
        if (record.getState() != SeatState.AVAILABLE) {
          throw new IllegalStateException("Seat not available: " + seatId);
        }
      }

      Instant expiresAt = now.plusSeconds(ttlSeconds);
      for (String seatId : seatIds) {
        inv.seatStateBySeatId.get(seatId).setLocked(lockId, expiresAt);
      }
    }
  }

  @Override
  public void confirmLock(
      String showId,
      List<String> seatIds,
      String lockId,
      String bookingId,
      Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }

    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);

      for (String seatId : seatIds) {
        SeatStateRecord record = inv.seatStateBySeatId.get(seatId);
        if (record == null) {
          throw new IllegalArgumentException("Unknown seatId: " + seatId);
        }
        if (record.getState() != SeatState.LOCKED) {
          throw new IllegalStateException("Seat not locked: " + seatId);
        }
        if (record.getLockedUntil() == null || !record.getLockedUntil().isAfter(now)) {
          throw new IllegalStateException("Lock expired for seat: " + seatId);
        }
        if (!lockId.equals(record.getLockId())) {
          throw new IllegalStateException("Seat locked by another lock: " + seatId);
        }
      }

      for (String seatId : seatIds) {
        inv.seatStateBySeatId.get(seatId).setBooked(bookingId);
      }
    }
  }

  @Override
  public void releaseLock(String showId, List<String> seatIds, String lockId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }

    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);

      for (String seatId : seatIds) {
        SeatStateRecord record = inv.seatStateBySeatId.get(seatId);
        if (record == null) {
          throw new IllegalArgumentException("Unknown seatId: " + seatId);
        }
        if (record.getState() != SeatState.LOCKED) {
          continue;
        }
        if (lockId.equals(record.getLockId())) {
          record.setAvailable();
        }
      }
    }
  }

  @Override
  public void cancelBookingSeats(
      String showId, List<String> seatIds, String bookingId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }

    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);

      for (String seatId : seatIds) {
        SeatStateRecord record = inv.seatStateBySeatId.get(seatId);
        if (record == null) {
          throw new IllegalArgumentException("Unknown seatId: " + seatId);
        }
        if (record.getState() == SeatState.BOOKED && bookingId.equals(record.getBookingId())) {
          record.setAvailable();
        } else {
          throw new IllegalStateException("Seat not booked by this booking: " + seatId);
        }
      }
    }
  }

  @Override
  public int countBookedSeats(String showId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);
      int count = 0;
      for (SeatStateRecord record : inv.seatStateBySeatId.values()) {
        if (record.getState() == SeatState.BOOKED) {
          count++;
        }
      }
      return count;
    }
  }

  @Override
  public int totalSeats(String showId) {
    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      return inv.seatStateBySeatId.size();
    }
  }

  @Override
  public void purgeExpiredLocks(String showId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    ShowInventory inv = inventoryFor(showId);
    synchronized (inv.lock) {
      purgeExpiredLocksInternal(inv, now);
    }
  }

  private void purgeExpiredLocksInternal(ShowInventory inv, Instant now) {
    List<String> toRelease = new ArrayList<>();
    for (Map.Entry<String, SeatStateRecord> entry : inv.seatStateBySeatId.entrySet()) {
      SeatStateRecord record = entry.getValue();
      if (record.getState() == SeatState.LOCKED) {
        Instant lockedUntil = record.getLockedUntil();
        if (lockedUntil == null || !lockedUntil.isAfter(now)) {
          toRelease.add(entry.getKey());
        }
      }
    }
    for (String seatId : toRelease) {
      inv.seatStateBySeatId.get(seatId).setAvailable();
    }
  }
}

