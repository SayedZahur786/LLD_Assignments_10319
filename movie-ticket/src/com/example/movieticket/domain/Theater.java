package com.example.movieticket.domain;

public class Theater {
  private final String theaterId;
  private final String name;
  private final String city;

  public Theater(String theaterId, String name, String city) {
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name");
    }
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("city");
    }
    this.theaterId = theaterId;
    this.name = name;
    this.city = city;
  }

  public String getTheaterId() {
    return theaterId;
  }

  public String getName() {
    return name;
  }

  public String getCity() {
    return city;
  }

  @Override
  public String toString() {
    return "Theater{theaterId='" + theaterId + "', name='" + name + "', city='" + city + "'}";
  }
}

