package com.example.movieticket.adapter;

public class FakePaymentGateway implements PaymentGateway {
  private final boolean alwaysSuccess;

  public FakePaymentGateway(boolean alwaysSuccess) {
    this.alwaysSuccess = alwaysSuccess;
  }

  @Override
  public boolean pay(String paymentId, long amountCents, String method) {
    if (paymentId == null || paymentId.isBlank()) {
      throw new IllegalArgumentException("paymentId");
    }
    if (amountCents < 0) {
      throw new IllegalArgumentException("amountCents");
    }
    if (method == null || method.isBlank()) {
      throw new IllegalArgumentException("method");
    }
    return alwaysSuccess;
  }

  @Override
  public boolean refund(String paymentId, long amountCents) {
    if (paymentId == null || paymentId.isBlank()) {
      throw new IllegalArgumentException("paymentId");
    }
    if (amountCents < 0) {
      throw new IllegalArgumentException("amountCents");
    }
    return alwaysSuccess;
  }
}

