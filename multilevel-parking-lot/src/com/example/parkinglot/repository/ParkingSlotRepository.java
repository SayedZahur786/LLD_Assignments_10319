package com.example.parkinglot.repository;

import com.example.parkinglot.domain.ParkingSlot;
import java.util.*;

public interface ParkingSlotRepository {
  ParkingSlot findById(String id);

  Collection<ParkingSlot> findAll();
}
