package com.example.movieticket.domain;

import java.time.Instant;

public class Payment {
  private final String paymentId;
  private final String bookingId;
  private final long amountCents;
  private PaymentStatus status;
  private final Instant createdAt;

  public Payment(String paymentId, String bookingId, long amountCents, PaymentStatus status, Instant createdAt) {
    if (paymentId == null || paymentId.isBlank()) {
      throw new IllegalArgumentException("paymentId");
    }
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    if (amountCents < 0) {
      throw new IllegalArgumentException("amountCents");
    }
    if (status == null) {
      throw new IllegalArgumentException("status");
    }
    if (createdAt == null) {
      throw new IllegalArgumentException("createdAt");
    }
    this.paymentId = paymentId;
    this.bookingId = bookingId;
    this.amountCents = amountCents;
    this.status = status;
    this.createdAt = createdAt;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public String getBookingId() {
    return bookingId;
  }

  public long getAmountCents() {
    return amountCents;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("status");
    }
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return "Payment{paymentId='" + paymentId + "', bookingId='" + bookingId + "', amountCents=" + amountCents + ", status=" + status + "}";
  }
}

