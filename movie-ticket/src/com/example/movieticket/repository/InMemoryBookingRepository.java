package com.example.movieticket.repository;

import com.example.movieticket.domain.Booking;
import java.util.*;

public class InMemoryBookingRepository implements BookingRepository {
  private final Map<String, Booking> bookingsById = new HashMap<>();

  @Override
  public void save(Booking booking) {
    if (booking == null) {
      throw new IllegalArgumentException("booking");
    }
    bookingsById.put(booking.getBookingId(), booking);
  }

  @Override
  public Booking findById(String bookingId) {
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    return bookingsById.get(bookingId);
  }

  @Override
  public Booking remove(String bookingId) {
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    return bookingsById.remove(bookingId);
  }
}

