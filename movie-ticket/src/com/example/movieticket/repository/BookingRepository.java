package com.example.movieticket.repository;

import com.example.movieticket.domain.Booking;
import java.util.*;

public interface BookingRepository {
  void save(Booking booking);

  Booking findById(String bookingId);

  Booking remove(String bookingId);
}

