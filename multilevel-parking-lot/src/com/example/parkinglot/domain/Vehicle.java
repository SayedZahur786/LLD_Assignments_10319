package com.example.parkinglot.domain;

public class Vehicle {
  private final String plateNumber;
  private final VehicleType vehicleType;

  public Vehicle(String plateNumber, VehicleType vehicleType) {
    if (plateNumber == null || plateNumber.isBlank()) {
      throw new IllegalArgumentException("plateNumber");
    }
    if (vehicleType == null) {
      throw new IllegalArgumentException("vehicleType");
    }
    this.plateNumber = plateNumber;
    this.vehicleType = vehicleType;
  }

  public String getPlateNumber() {
    return plateNumber;
  }

  public VehicleType getVehicleType() {
    return vehicleType;
  }

  @Override
  public String toString() {
    return plateNumber + " " + vehicleType;
  }
}
