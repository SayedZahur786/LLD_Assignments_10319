package com.example.movieticket.repository;

import com.example.movieticket.domain.SeatLock;
import java.util.*;

public class InMemorySeatLockRepository implements SeatLockRepository {
  private final Map<String, SeatLock> locksById = new HashMap<>();

  @Override
  public void save(SeatLock lock) {
    if (lock == null) {
      throw new IllegalArgumentException("lock");
    }
    locksById.put(lock.getLockId(), lock);
  }

  @Override
  public SeatLock findById(String lockId) {
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    return locksById.get(lockId);
  }

  @Override
  public SeatLock remove(String lockId) {
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    return locksById.remove(lockId);
  }
}

