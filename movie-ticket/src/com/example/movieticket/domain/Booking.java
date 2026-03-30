package com.example.movieticket.domain;

import java.time.Instant;
import java.util.*;

public class Booking {
  private final String bookingId;
  private final String showId;
  private final List<String> seatIds;
  private final long totalPriceCents;
  private final Instant createdAt;
  private BookingStatus status;
  private String paymentId;

  public Booking(
      String bookingId,
      String showId,
      List<String> seatIds,
      long totalPriceCents,
      Instant createdAt) {
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (totalPriceCents < 0) {
      throw new IllegalArgumentException("totalPriceCents");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("createdAt");
    }

    this.bookingId = bookingId;
    this.showId = showId;
    this.seatIds = List.copyOf(seatIds);
    this.totalPriceCents = totalPriceCents;
    this.createdAt = createdAt;
    this.status = BookingStatus.PENDING_PAYMENT;
  }

  public String getBookingId() {
    return bookingId;
  }

  public String getShowId() {
    return showId;
  }

  public List<String> getSeatIds() {
    return seatIds;
  }

  public long getTotalPriceCents() {
    return totalPriceCents;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public BookingStatus getStatus() {
    return status;
  }

  public void setStatus(BookingStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("status");
    }
    this.status = status;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    if (paymentId == null || paymentId.isBlank()) {
      throw new IllegalArgumentException("paymentId");
    }
    this.paymentId = paymentId;
  }

  @Override
  public String toString() {
    return "Booking{bookingId='" + bookingId + "', showId='" + showId + "', seats=" + seatIds + ", status=" + status + "}";
  }
}

