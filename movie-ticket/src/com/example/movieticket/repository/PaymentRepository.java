package com.example.movieticket.repository;

import com.example.movieticket.domain.Payment;
import java.util.*;

public interface PaymentRepository {
  void save(Payment payment);

  Payment findById(String paymentId);

  Collection<Payment> findAll();
}

