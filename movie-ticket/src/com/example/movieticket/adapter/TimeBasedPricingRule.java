package com.example.movieticket.adapter;

import com.example.movieticket.domain.PricingPolicy;
import java.time.*;

public class TimeBasedPricingRule {
  private final PricingPolicy pricingPolicy;

  public TimeBasedPricingRule(PricingPolicy pricingPolicy) {
    if (pricingPolicy == null) {
      throw new IllegalArgumentException("pricingPolicy");
    }
    this.pricingPolicy = pricingPolicy;
  }

  public double multiplier(Instant showStartTime) {
    if (showStartTime == null) {
      throw new IllegalArgumentException("showStartTime");
    }

    ZonedDateTime zdt = showStartTime.atZone(ZoneOffset.UTC);
    DayOfWeek day = zdt.getDayOfWeek();
    int hour = zdt.getHour();

    boolean isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    if (isWeekend) {
      return pricingPolicy.getWeekendMultiplier();
    }

    int primeStart = pricingPolicy.getPrimeStartHourUtc();
    int primeEnd = pricingPolicy.getPrimeEndHourUtc();

    boolean inPrimeTime;
    if (primeStart <= primeEnd) {
      inPrimeTime = hour >= primeStart && hour < primeEnd;
    } else {
      inPrimeTime = hour >= primeStart || hour < primeEnd;
    }

    if (inPrimeTime) {
      return pricingPolicy.getPrimeTimeMultiplier();
    }

    return pricingPolicy.getOffTimeMultiplier();
  }
}

