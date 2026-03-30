package com.example.elevator.repository;

import com.example.elevator.domain.HallCall;
import java.util.*;

public interface HallCallRepository {
  HallCall save(HallCall hallCall);

  HallCall findById(String hallCallId);

  List<HallCall> findAll();

  void assignToElevator(String hallCallId, String elevatorId);

  List<HallCall> findUnservedAssignedToElevatorAtFloor(String elevatorId, int floor);

  List<HallCall> findUnservedAssignedToElevator(String elevatorId);

  void markServed(String hallCallId);
}
