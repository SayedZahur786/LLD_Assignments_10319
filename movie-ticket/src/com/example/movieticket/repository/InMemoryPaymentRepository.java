package com.example.movieticket.repository;

import com.example.movieticket.domain.Payment;
import java.util.*;

public class InMemoryPaymentRepository implements PaymentRepository {
  private final Map<String, Payment> paymentsById = new HashMap<>();

  @Override
  public void save(Payment payment) {
    if (payment == null) {
      throw new IllegalArgumentException("payment");
    }
    paymentsById.put(payment.getPaymentId(), payment);
  }

  @Override
  public Payment findById(String paymentId) {
    if (paymentId == null || paymentId.isBlank()) {
      throw new IllegalArgumentException("paymentId");
    }
    return paymentsById.get(paymentId);
  }

  @Override
  public Collection<Payment> findAll() {
    return Collections.unmodifiableCollection(paymentsById.values());
  }
}

