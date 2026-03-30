package com.example.movieticket.repository;

import com.example.movieticket.domain.*;
import java.time.Instant;
import java.util.*;

public interface SeatInventoryRepository {
  void initializeShow(Show show);

  List<SeatView> getSeatMap(String showId, Instant now);

  void lockSeats(
      String showId,
      List<String> seatIds,
      String lockId,
      String userId,
      Instant now,
      long ttlSeconds);

  void confirmLock(String showId, List<String> seatIds, String lockId, String bookingId, Instant now);

  void releaseLock(String showId, List<String> seatIds, String lockId, Instant now);

  void cancelBookingSeats(String showId, List<String> seatIds, String bookingId, Instant now);

  int countBookedSeats(String showId, Instant now);

  int totalSeats(String showId);

  void purgeExpiredLocks(String showId, Instant now);
}

