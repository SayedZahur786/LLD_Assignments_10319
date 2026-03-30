package com.example.elevator.adapter;

import com.example.elevator.domain.ElevatorCarSpec;
import com.example.elevator.domain.ExpressStopPolicy;

public class AllowlistExpressStopPolicy implements ExpressStopPolicy {
  @Override
  public boolean canStopAt(ElevatorCarSpec spec, int floor) {
    if (spec == null) {
      throw new IllegalArgumentException("spec");
    }
    if (!spec.isExpressEnabled()) {
      return true;
    }
    return spec.getExpressAllowedStops().contains(floor);
  }
}
