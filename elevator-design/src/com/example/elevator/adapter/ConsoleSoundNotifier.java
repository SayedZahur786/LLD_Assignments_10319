package com.example.elevator.adapter;

import com.example.elevator.domain.EmergencyType;
import com.example.elevator.domain.SoundNotifier;

public class ConsoleSoundNotifier implements SoundNotifier {
  @Override
  public void playWeightLimitExceededSound(
      String elevatorId, int floor, int currentLoadKg, int capacityKg) {
    System.out.println(
        "SOUND: BEEP (weight limit exceeded) elevator="
            + elevatorId
            + " floor="
            + floor
            + " load="
            + currentLoadKg
            + "kg capacity="
            + capacityKg
            + "kg");
  }

  @Override
  public void playEmergencyAlarmSound(String elevatorId, EmergencyType type) {
    System.out.println("SOUND: ALARM (emergency=" + type + ") elevator=" + elevatorId);
  }
}
