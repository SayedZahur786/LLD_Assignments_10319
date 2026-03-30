package com.example.movieticket.adapter;

import com.example.movieticket.domain.PricingPolicy;

public class DemandBasedPricingRule {
  private final PricingPolicy pricingPolicy;

  public DemandBasedPricingRule(PricingPolicy pricingPolicy) {
    if (pricingPolicy == null) {
      throw new IllegalArgumentException("pricingPolicy");
    }
    this.pricingPolicy = pricingPolicy;
  }

  public double multiplier(int bookedSeats, int totalSeats) {
    if (totalSeats <= 0) {
      throw new IllegalArgumentException("totalSeats");
    }
    if (bookedSeats < 0) {
      throw new IllegalArgumentException("bookedSeats");
    }

    double ratio = bookedSeats / (double) totalSeats;
    if (ratio < 0.5) {
      return pricingPolicy.getSurgeLowMultiplier();
    }
    if (ratio < 0.8) {
      return pricingPolicy.getSurgeMidMultiplier();
    }
    return pricingPolicy.getSurgeHighMultiplier();
  }
}

