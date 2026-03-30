package com.example.elevator.repository;

import com.example.elevator.domain.ElevatorCar;
import java.util.*;

public class InMemoryElevatorCarRepository implements ElevatorCarRepository {
  private final Map<String, ElevatorCar> elevatorsById = new HashMap<>();

  public InMemoryElevatorCarRepository(List<ElevatorCar> initialElevators) {
    if (initialElevators == null || initialElevators.isEmpty()) {
      throw new IllegalArgumentException("initialElevators required");
    }
    for (ElevatorCar car : initialElevators) {
      elevatorsById.put(car.getElevatorId(), car);
    }
  }

  @Override
  public ElevatorCar findById(String elevatorId) {
    if (elevatorId == null) {
      throw new IllegalArgumentException("elevatorId");
    }
    synchronized (this) {
      return elevatorsById.get(elevatorId);
    }
  }

  @Override
  public List<ElevatorCar> findAll() {
    synchronized (this) {
      return new ArrayList<>(elevatorsById.values());
    }
  }

  @Override
  public void save(ElevatorCar elevatorCar) {
    if (elevatorCar == null) {
      throw new IllegalArgumentException("elevatorCar");
    }
    synchronized (this) {
      elevatorsById.put(elevatorCar.getElevatorId(), elevatorCar);
    }
  }

  public void addElevator(ElevatorCar elevatorCar) {
    save(elevatorCar);
  }
}
