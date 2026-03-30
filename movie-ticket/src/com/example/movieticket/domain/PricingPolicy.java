package com.example.movieticket.domain;

import java.util.*;

public class PricingPolicy {
  private final Map<SeatCategory, Long> basePriceCentsByCategory;

  private final int primeStartHourUtc;
  private final int primeEndHourUtc;
  private final double primeTimeMultiplier;
  private final double offTimeMultiplier;

  private final double weekendMultiplier;

  private final double surgeLowMultiplier;
  private final double surgeMidMultiplier;
  private final double surgeHighMultiplier;

  public PricingPolicy(
      Map<SeatCategory, Long> basePriceCentsByCategory,
      int primeStartHourUtc,
      int primeEndHourUtc,
      double primeTimeMultiplier,
      double offTimeMultiplier,
      double weekendMultiplier,
      double surgeLowMultiplier,
      double surgeMidMultiplier,
      double surgeHighMultiplier) {
    if (basePriceCentsByCategory == null || basePriceCentsByCategory.isEmpty()) {
      throw new IllegalArgumentException("basePriceCentsByCategory");
    }
    if (primeStartHourUtc < 0 || primeStartHourUtc > 23) {
      throw new IllegalArgumentException("primeStartHourUtc");
    }
    if (primeEndHourUtc < 0 || primeEndHourUtc > 23) {
      throw new IllegalArgumentException("primeEndHourUtc");
    }
    if (primeTimeMultiplier <= 0 || offTimeMultiplier <= 0 || weekendMultiplier <= 0) {
      throw new IllegalArgumentException("multipliers must be > 0");
    }
    if (surgeLowMultiplier <= 0 || surgeMidMultiplier <= 0 || surgeHighMultiplier <= 0) {
      throw new IllegalArgumentException("surge multipliers must be > 0");
    }

    this.basePriceCentsByCategory = new EnumMap<>(SeatCategory.class);
    this.basePriceCentsByCategory.putAll(basePriceCentsByCategory);
    this.primeStartHourUtc = primeStartHourUtc;
    this.primeEndHourUtc = primeEndHourUtc;
    this.primeTimeMultiplier = primeTimeMultiplier;
    this.offTimeMultiplier = offTimeMultiplier;
    this.weekendMultiplier = weekendMultiplier;
    this.surgeLowMultiplier = surgeLowMultiplier;
    this.surgeMidMultiplier = surgeMidMultiplier;
    this.surgeHighMultiplier = surgeHighMultiplier;
  }

  public long basePriceCents(SeatCategory category) {
    if (category == null) {
      throw new IllegalArgumentException("category");
    }
    Long price = basePriceCentsByCategory.get(category);
    if (price == null) {
      throw new IllegalArgumentException("missing base price for " + category);
    }
    return price;
  }

  public Map<SeatCategory, Long> getBasePriceCentsByCategory() {
    return Collections.unmodifiableMap(basePriceCentsByCategory);
  }

  public int getPrimeStartHourUtc() {
    return primeStartHourUtc;
  }

  public int getPrimeEndHourUtc() {
    return primeEndHourUtc;
  }

  public double getPrimeTimeMultiplier() {
    return primeTimeMultiplier;
  }

  public double getOffTimeMultiplier() {
    return offTimeMultiplier;
  }

  public double getWeekendMultiplier() {
    return weekendMultiplier;
  }

  public double getSurgeLowMultiplier() {
    return surgeLowMultiplier;
  }

  public double getSurgeMidMultiplier() {
    return surgeMidMultiplier;
  }

  public double getSurgeHighMultiplier() {
    return surgeHighMultiplier;
  }
}

