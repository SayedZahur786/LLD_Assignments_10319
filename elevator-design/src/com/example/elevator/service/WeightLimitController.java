package com.example.elevator.service;

import com.example.elevator.domain.AnnouncementKey;
import com.example.elevator.domain.AnnouncementService;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.SoundNotifier;
import com.example.elevator.repository.ElevatorCarRepository;
import java.util.*;

public class WeightLimitController {
  private final Map<String, Object> elevatorLocks;
  private final ElevatorCarRepository elevatorCarRepository;
  private final AnnouncementService announcementService;
  private final SoundNotifier soundNotifier;

  public WeightLimitController(
      Map<String, Object> elevatorLocks,
      ElevatorCarRepository elevatorCarRepository,
      AnnouncementService announcementService,
      SoundNotifier soundNotifier) {
    if (elevatorLocks == null || elevatorCarRepository == null) {
      throw new IllegalArgumentException("elevatorLocks and elevatorCarRepository required");
    }
    if (announcementService == null || soundNotifier == null) {
      throw new IllegalArgumentException("announcementService and soundNotifier required");
    }
    this.elevatorLocks = elevatorLocks;
    this.elevatorCarRepository = elevatorCarRepository;
    this.announcementService = announcementService;
    this.soundNotifier = soundNotifier;
  }

  public boolean updateElevatorLoadKg(String elevatorId, int loadKg, Locale locale) {
    if (elevatorId == null || elevatorId.isBlank()) {
      throw new IllegalArgumentException("elevatorId");
    }
    if (loadKg < 0) throw new IllegalArgumentException("loadKg");

    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) throw new IllegalStateException("Missing lock for elevator: " + elevatorId);

    synchronized (lock) {
      ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
      if (elevator == null) throw new IllegalStateException("Unknown elevator: " + elevatorId);

      elevator.setCurrentLoadKg(loadKg);

      if (elevator.isOverweight()) {
        // Sound + keep door open when entering an overweight state.
        if (!elevator.isOverweightAlarmActive()) {
          elevator.setOverweightAlarmActive(true);

          Map<String, String> params = new HashMap<>();
          params.put("elevatorId", elevatorId);
          params.put("floor", String.valueOf(elevator.getCurrentFloor()));
          params.put("currentLoadKg", String.valueOf(loadKg));
          params.put("capacityKg", String.valueOf(elevator.getSpec().getCapacityKg()));

          announcementService.announce(AnnouncementKey.WEIGHT_LIMIT_EXCEEDED, locale, params);
          soundNotifier.playWeightLimitExceededSound(
              elevatorId, elevator.getCurrentFloor(), loadKg, elevator.getSpec().getCapacityKg());
        }
        elevator.setDoorOpen(true);
        elevator.setState(ElevatorState.IDLE);
      } else {
        elevator.setOverweightAlarmActive(false);
      }
      return true;
    }
  }
}
