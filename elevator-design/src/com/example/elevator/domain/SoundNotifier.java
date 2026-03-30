package com.example.elevator.domain;

public interface SoundNotifier {
  void playWeightLimitExceededSound(
      String elevatorId, int floor, int currentLoadKg, int capacityKg);

  void playEmergencyAlarmSound(String elevatorId, EmergencyType type);
}
