package com.example.parkinglot.controller;

import com.example.parkinglot.domain.Bill;
import com.example.parkinglot.domain.ParkingTicket;
import com.example.parkinglot.domain.SlotAvailability;
import com.example.parkinglot.domain.SlotType;
import com.example.parkinglot.domain.Vehicle;
import com.example.parkinglot.service.ParkingLotService;
import java.time.Instant;

public class ParkingLotController {
  private final ParkingLotService parkingLotService;

  public ParkingLotController(ParkingLotService parkingLotService) {
    if (parkingLotService == null) {
      throw new IllegalArgumentException("parkingLotService");
    }
    this.parkingLotService = parkingLotService;
  }

  public ParkingTicket park(
      Vehicle vehicleDetails, Instant entryTime, SlotType requestedSlotType, String entryGateID) {
    return parkingLotService.park(vehicleDetails, entryTime, requestedSlotType, entryGateID);
  }

  public SlotAvailability status() {
    return parkingLotService.status();
  }

  public Bill exit(ParkingTicket parkingTicket, Instant exitTime) {
    return parkingLotService.exit(parkingTicket, exitTime);
  }
}
