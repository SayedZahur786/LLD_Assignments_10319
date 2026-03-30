package com.example.elevator.domain;

import java.util.*;

public interface ElevatorDispatchStrategy {
  String assignElevator(
      HallCall hallCall, List<ElevatorCar> candidateElevators, ExpressStopPolicy expressStopPolicy);
}
