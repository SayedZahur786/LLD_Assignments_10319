package com.example.parkinglot.domain;

import java.time.Duration;

public class Bill {
  private final String ticketId;
  private final Duration parkedDuration;
  private final long billableHours;
  private final long amountCents;

  public Bill(String ticketId, Duration parkedDuration, long billableHours, long amountCents) {
    if (ticketId == null || ticketId.isBlank()) {
      throw new IllegalArgumentException("ticketId");
    }
    if (parkedDuration == null || billableHours <= 0 || amountCents < 0) {
      throw new IllegalArgumentException("invalid bill");
    }
    this.ticketId = ticketId;
    this.parkedDuration = parkedDuration;
    this.billableHours = billableHours;
    this.amountCents = amountCents;
  }

  public String getTicketId() {
    return ticketId;
  }

  public Duration getParkedDuration() {
    return parkedDuration;
  }

  public long getBillableHours() {
    return billableHours;
  }

  public long getAmountCents() {
    return amountCents;
  }

  @Override
  public String toString() {
    return ticketId + " " + billableHours + "h " + amountCents + "¢ " + parkedDuration;
  }
}
