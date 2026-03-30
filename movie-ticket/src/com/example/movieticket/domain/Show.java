package com.example.movieticket.domain;

import java.time.Instant;
import java.util.*;

public class Show {
  private final String showId;
  private final String movieId;
  private final String theaterId;
  private final String screenId;
  private final Instant startTime;
  private final Map<String, SeatCategory> seatCategoryBySeatId;
  private final PricingPolicy pricingPolicy;

  public Show(
      String showId,
      String movieId,
      String theaterId,
      String screenId,
      Instant startTime,
      Map<String, SeatCategory> seatCategoryBySeatId,
      PricingPolicy pricingPolicy) {
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    if (movieId == null || movieId.isBlank()) {
      throw new IllegalArgumentException("movieId");
    }
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    if (screenId == null || screenId.isBlank()) {
      throw new IllegalArgumentException("screenId");
    }
    if (startTime == null) {
      throw new IllegalArgumentException("startTime");
    }
    if (seatCategoryBySeatId == null || seatCategoryBySeatId.isEmpty()) {
      throw new IllegalArgumentException("seatCategoryBySeatId");
    }
    if (pricingPolicy == null) {
      throw new IllegalArgumentException("pricingPolicy");
    }

    this.showId = showId;
    this.movieId = movieId;
    this.theaterId = theaterId;
    this.screenId = screenId;
    this.startTime = startTime;
    this.seatCategoryBySeatId = new HashMap<>(seatCategoryBySeatId);
    this.pricingPolicy = pricingPolicy;
  }

  public String getShowId() {
    return showId;
  }

  public String getMovieId() {
    return movieId;
  }

  public String getTheaterId() {
    return theaterId;
  }

  public String getScreenId() {
    return screenId;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Map<String, SeatCategory> getSeatCategoryBySeatId() {
    return Collections.unmodifiableMap(seatCategoryBySeatId);
  }

  public PricingPolicy getPricingPolicy() {
    return pricingPolicy;
  }

  public int totalSeats() {
    return seatCategoryBySeatId.size();
  }

  public Set<String> seatIds() {
    return Collections.unmodifiableSet(seatCategoryBySeatId.keySet());
  }

  @Override
  public String toString() {
    return "Show{showId='"
        + showId
        + "', movieId='"
        + movieId
        + "', theaterId='"
        + theaterId
        + "', screenId='"
        + screenId
        + "', startTime="
        + startTime
        + ", totalSeats="
        + totalSeats()
        + "}";
  }
}

