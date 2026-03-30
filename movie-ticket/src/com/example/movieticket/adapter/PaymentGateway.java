package com.example.movieticket.adapter;

public interface PaymentGateway {
  boolean pay(String paymentId, long amountCents, String method);

  boolean refund(String paymentId, long amountCents);
}

