package com.example.movieticket.repository;

import com.example.movieticket.domain.SeatLock;
import java.util.*;

public interface SeatLockRepository {
  void save(SeatLock lock);

  SeatLock findById(String lockId);

  SeatLock remove(String lockId);
}

