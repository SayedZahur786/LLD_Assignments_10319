package com.example.elevator.repository;

import com.example.elevator.domain.ElevatorCar;
import java.util.*;

public interface ElevatorCarRepository {
  ElevatorCar findById(String elevatorId);

  List<ElevatorCar> findAll();

  void save(ElevatorCar elevatorCar);
}
