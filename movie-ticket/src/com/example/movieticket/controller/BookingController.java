package com.example.movieticket.controller;

import com.example.movieticket.domain.*;
import com.example.movieticket.service.MovieTicketService;
import java.time.Instant;
import java.util.*;

public class BookingController {
  private final MovieTicketService movieTicketService;

  public BookingController(MovieTicketService movieTicketService) {
    if (movieTicketService == null) {
      throw new IllegalArgumentException("movieTicketService");
    }
    this.movieTicketService = movieTicketService;
  }

  public List<Movie> viewMoviesInCity(String city) {
    return movieTicketService.viewMoviesInCity(city);
  }

  public List<Theater> viewTheatersInCity(String city) {
    return movieTicketService.viewTheatersInCity(city);
  }

  public List<Show> viewShowsForMovie(String movieId) {
    return movieTicketService.viewShowsForMovie(movieId);
  }

  public List<Show> viewShowsForTheater(String theaterId) {
    return movieTicketService.viewShowsForTheater(theaterId);
  }

  public List<SeatView> viewSeatMap(String showId, Instant now) {
    return movieTicketService.viewSeatMap(showId, now);
  }

  public SeatLock lockSeats(String showId, List<String> seatIds, String userId, Instant now) {
    return movieTicketService.lockSeats(showId, seatIds, userId, now);
  }

  public Booking bookAndPay(String lockId, String paymentMethod, Instant now) {
    return movieTicketService.bookAndPay(lockId, paymentMethod, now);
  }

  public Booking cancelBooking(String bookingId, Instant now) {
    return movieTicketService.cancelBooking(bookingId, now);
  }
}

