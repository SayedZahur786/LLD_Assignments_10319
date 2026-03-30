package com.example.movieticket.adapter;

import com.example.movieticket.domain.*;
import com.example.movieticket.repository.SeatInventoryRepository;
import java.time.Instant;
import java.util.*;

public class SeatPricingCalculator {
  private final SeatInventoryRepository seatInventoryRepository;

  public SeatPricingCalculator(SeatInventoryRepository seatInventoryRepository) {
    if (seatInventoryRepository == null) {
      throw new IllegalArgumentException("seatInventoryRepository");
    }
    this.seatInventoryRepository = seatInventoryRepository;
  }

  public long calculateTotalPriceCents(Show show, List<String> seatIds, Instant now) {
    if (show == null) {
      throw new IllegalArgumentException("show");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (now == null) {
      throw new IllegalArgumentException("now");
    }

    PricingPolicy pricingPolicy = show.getPricingPolicy();
    TimeBasedPricingRule timeRule = new TimeBasedPricingRule(pricingPolicy);
    DemandBasedPricingRule demandRule = new DemandBasedPricingRule(pricingPolicy);

    int totalSeats = show.totalSeats();
    int bookedSeats = seatInventoryRepository.countBookedSeats(show.getShowId(), now);

    double timeMultiplier = timeRule.multiplier(show.getStartTime());
    double demandMultiplier = demandRule.multiplier(bookedSeats, totalSeats);

    long total = 0;
    for (String seatId : seatIds) {
      SeatCategory category = show.getSeatCategoryBySeatId().get(seatId);
      if (category == null) {
        throw new IllegalArgumentException("Unknown seatId: " + seatId);
      }
      long base = pricingPolicy.basePriceCents(category);
      long seatPrice =
          (long) Math.round(base * timeMultiplier * demandMultiplier);
      total += seatPrice;
    }
    return total;
  }
}

