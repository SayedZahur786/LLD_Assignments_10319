package com.example.elevator.adapter;

import com.example.elevator.domain.CallPriority;
import com.example.elevator.domain.Direction;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorCarSpec;
import com.example.elevator.domain.ElevatorDispatchStrategy;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.ExpressStopPolicy;
import com.example.elevator.domain.HallCall;
import java.util.*;

public class LookDispatchStrategy implements ElevatorDispatchStrategy {
  private static final int REVERSE_PENALTY_FLOORS = 5;
  private static final int PRIORITIZED_BONUS = 10_000;

  @Override
  public String assignElevator(
      HallCall hallCall,
      List<ElevatorCar> candidateElevators,
      ExpressStopPolicy expressStopPolicy) {
    if (hallCall == null) {
      throw new IllegalArgumentException("hallCall");
    }
    if (candidateElevators == null || candidateElevators.isEmpty()) {
      throw new IllegalArgumentException("candidateElevators");
    }

    String bestElevatorId = null;
    long bestScore = Long.MAX_VALUE;

    for (ElevatorCar elevator : candidateElevators) {
      if (elevator == null) continue;

      if (elevator.getState() == ElevatorState.UNDER_MAINTENANCE) {
        continue;
      }

      ElevatorCarSpec spec = elevator.getSpec();
      if (expressStopPolicy != null && !expressStopPolicy.canStopAt(spec, hallCall.getFloor())) {
        continue;
      }

      long score = computeScore(hallCall, elevator);
      if (hallCall.getPriority() == CallPriority.PRIORITIZED) {
        score -= PRIORITIZED_BONUS;
      }

      if (bestElevatorId == null || score < bestScore) {
        bestElevatorId = elevator.getElevatorId();
        bestScore = score;
      } else if (score == bestScore && elevator.getElevatorId() != null) {
        if (elevator.getElevatorId().compareTo(bestElevatorId) < 0) {
          bestElevatorId = elevator.getElevatorId();
        }
      }
    }

    return bestElevatorId;
  }

  private long computeScore(HallCall call, ElevatorCar elevator) {
    int currentFloor = elevator.getCurrentFloor();
    Set<Integer> stops = elevator.stopFloorsSnapshot();
    ElevatorState state = elevator.getState();

    if (state == ElevatorState.IDLE) {
      return Math.abs(call.getFloor() - currentFloor);
    }

    if (state == ElevatorState.MOVING_UP) {
      if (call.getFloor() >= currentFloor) {
        return (call.getFloor() - currentFloor)
            + intermediateStopsCost(stops, currentFloor, call.getFloor(), Direction.UP);
      }
      int reverseDistance = currentFloor - call.getFloor();
      return reverseDistance
          + REVERSE_PENALTY_FLOORS
          + intermediateStopsCost(stops, call.getFloor(), currentFloor, Direction.DOWN);
    }

    if (state == ElevatorState.MOVING_DOWN) {
      if (call.getFloor() <= currentFloor) {
        return (currentFloor - call.getFloor())
            + intermediateStopsCost(stops, call.getFloor(), currentFloor, Direction.DOWN);
      }
      int reverseDistance = call.getFloor() - currentFloor;
      return reverseDistance
          + REVERSE_PENALTY_FLOORS
          + intermediateStopsCost(stops, currentFloor, call.getFloor(), Direction.UP);
    }
    return Long.MAX_VALUE;
  }

  private int intermediateStopsCost(
      Set<Integer> stops, int fromInclusive, int toInclusive, Direction dir) {
    if (stops == null || stops.isEmpty()) return 0;
    int cost = 0;
    if (dir == Direction.UP) {
      for (int stop : stops) {
        if (stop > fromInclusive && stop < toInclusive) {
          cost++;
        }
      }
    } else {
      for (int stop : stops) {
        if (stop < fromInclusive && stop > toInclusive) {
          cost++;
        }
      }
    }
    return cost;
  }
}
