package com.example.elevator.service;

import com.example.elevator.domain.AnnouncementKey;
import com.example.elevator.domain.AnnouncementService;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.EmergencyType;
import com.example.elevator.domain.SoundNotifier;
import com.example.elevator.repository.ElevatorCarRepository;
import java.util.*;

public class EmergencyController {
  private final Object hallDispatchLock;
  private final Map<String, Object> elevatorLocks;
  private final ElevatorCarRepository elevatorCarRepository;
  private final AnnouncementService announcementService;
  private final SoundNotifier soundNotifier;

  private volatile boolean emergencyActive;
  private volatile EmergencyType emergencyType;

  public EmergencyController(
      Object hallDispatchLock,
      Map<String, Object> elevatorLocks,
      ElevatorCarRepository elevatorCarRepository,
      AnnouncementService announcementService,
      SoundNotifier soundNotifier) {
    if (hallDispatchLock == null
        || elevatorLocks == null
        || elevatorCarRepository == null
        || announcementService == null
        || soundNotifier == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }
    this.hallDispatchLock = hallDispatchLock;
    this.elevatorLocks = elevatorLocks;
    this.elevatorCarRepository = elevatorCarRepository;
    this.announcementService = announcementService;
    this.soundNotifier = soundNotifier;
    this.emergencyActive = false;
    this.emergencyType = null;
  }

  public void triggerEmergency(EmergencyType type, Locale locale) {
    if (type == null) {
      throw new IllegalArgumentException("type");
    }

    synchronized (hallDispatchLock) {
      emergencyActive = true;
      emergencyType = type;

      Map<String, String> params = new HashMap<>();
      params.put("emergencyType", type.toString());
      announcementService.announce(AnnouncementKey.EMERGENCY_POWER_OUTAGE, locale, params);

      for (ElevatorCar car : elevatorCarRepository.findAll()) {
        Object lock = elevatorLocks.get(car.getElevatorId());
        if (lock == null) continue;
        synchronized (lock) {
          if (car.getState() != ElevatorState.UNDER_MAINTENANCE) {
            car.setState(ElevatorState.IDLE);
          }
          car.setDoorOpen(true);
        }
        soundNotifier.playEmergencyAlarmSound(car.getElevatorId(), type);
      }
    }
  }

  public void clearEmergency() {
    emergencyActive = false;
    emergencyType = null;
  }

  public boolean isEmergencyActive() {
    return emergencyActive;
  }

  public EmergencyType getEmergencyType() {
    return emergencyType;
  }
}
