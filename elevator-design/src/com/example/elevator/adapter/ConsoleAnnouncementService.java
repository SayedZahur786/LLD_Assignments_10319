package com.example.elevator.adapter;

import com.example.elevator.domain.AnnouncementKey;
import com.example.elevator.domain.AnnouncementService;
import java.util.*;

public class ConsoleAnnouncementService implements AnnouncementService {
  @Override
  public void announce(AnnouncementKey key, Locale locale, Map<String, String> params) {
    String lang = (locale == null) ? "en" : locale.getLanguage();
    String elevatorId = params != null ? params.get("elevatorId") : null;
    String floor = params != null ? params.get("floor") : null;
    String currentLoad = params != null ? params.get("currentLoadKg") : null;
    String capacity = params != null ? params.get("capacityKg") : null;

    String message;
    switch (key) {
      case WEIGHT_LIMIT_EXCEEDED:
        if ("hi".equalsIgnoreCase(lang)) {
          message =
              "वजन सीमा पार: एलिवेटर "
                  + elevatorId
                  + ", फ्लोर "
                  + floor
                  + ". दरवाजे खुले रहेंगे (लोड="
                  + currentLoad
                  + "kg, क्षमता="
                  + capacity
                  + "kg)।";
        } else {
          message =
              "WEIGHT LIMIT EXCEEDED on elevator "
                  + elevatorId
                  + " at floor "
                  + floor
                  + ". Doors kept open (load="
                  + currentLoad
                  + "kg, capacity="
                  + capacity
                  + "kg).";
        }
        break;
      case EMERGENCY_POWER_OUTAGE:
        if ("hi".equalsIgnoreCase(lang)) {
          message = "आपातकाल: पावर आउटेज. एलिवेटर मूव नहीं करेंगे, दरवाजे खुलेंगे।";
        } else {
          message = "EMERGENCY: POWER OUTAGE. Elevators will not move; doors opened.";
        }
        break;
      case ELEVATOR_ASSIGNED:
        if ("hi".equalsIgnoreCase(lang)) {
          message =
              "कॉल स्वीकार: एलिवेटर " + elevatorId + " को असाइन किया गया (फ्लोर " + floor + ").";
        } else {
          message = "CALL ACCEPTED: elevator " + elevatorId + " assigned for floor " + floor + ".";
        }
        break;
      default:
        message = key.toString();
    }
    System.out.println(message);
  }
}
