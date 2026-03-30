package com.example.movieticket.domain;

public class Screen {
  private final String screenId;
  private final String theaterId;
  private final String name;

  public Screen(String screenId, String theaterId, String name) {
    if (screenId == null || screenId.isBlank()) {
      throw new IllegalArgumentException("screenId");
    }
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("name");
    }
    this.screenId = screenId;
    this.theaterId = theaterId;
    this.name = name;
  }

  public String getScreenId() {
    return screenId;
  }

  public String getTheaterId() {
    return theaterId;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Screen{screenId='" + screenId + "', theaterId='" + theaterId + "', name='" + name + "'}";
  }
}

