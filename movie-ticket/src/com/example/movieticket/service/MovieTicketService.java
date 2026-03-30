package com.example.movieticket.service;

import com.example.movieticket.adapter.*;
import com.example.movieticket.domain.*;
import com.example.movieticket.repository.*;
import java.time.Instant;
import java.util.*;

public class MovieTicketService {
  private static final long DEFAULT_LOCK_TTL_SECONDS = 300;

  private final MovieRepository movieRepository;
  private final TheaterRepository theaterRepository;
  private final ScreenRepository screenRepository;
  private final ShowRepository showRepository;

  private final SeatInventoryRepository seatInventoryRepository;
  private final SeatLockRepository seatLockRepository;

  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;

  private final PaymentGateway paymentGateway;
  private final SeatPricingCalculator seatPricingCalculator;

  public MovieTicketService(
      MovieRepository movieRepository,
      TheaterRepository theaterRepository,
      ScreenRepository screenRepository,
      ShowRepository showRepository,
      SeatInventoryRepository seatInventoryRepository,
      SeatLockRepository seatLockRepository,
      BookingRepository bookingRepository,
      PaymentRepository paymentRepository,
      PaymentGateway paymentGateway,
      SeatPricingCalculator seatPricingCalculator) {
    if (movieRepository == null
        || theaterRepository == null
        || screenRepository == null
        || showRepository == null
        || seatInventoryRepository == null
        || seatLockRepository == null
        || bookingRepository == null
        || paymentRepository == null
        || paymentGateway == null
        || seatPricingCalculator == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }

    this.movieRepository = movieRepository;
    this.theaterRepository = theaterRepository;
    this.screenRepository = screenRepository;
    this.showRepository = showRepository;
    this.seatInventoryRepository = seatInventoryRepository;
    this.seatLockRepository = seatLockRepository;
    this.bookingRepository = bookingRepository;
    this.paymentRepository = paymentRepository;
    this.paymentGateway = paymentGateway;
    this.seatPricingCalculator = seatPricingCalculator;
  }

  public List<Movie> viewMoviesInCity(String city) {
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("city");
    }

    List<Theater> theaters = theaterRepository.findByCity(city);
    Set<String> movieIds = new HashSet<>();
    for (Theater theater : theaters) {
      List<Show> shows = showRepository.findByTheaterId(theater.getTheaterId());
      for (Show show : shows) {
        movieIds.add(show.getMovieId());
      }
    }

    List<Movie> result = new ArrayList<>();
    for (String movieId : movieIds) {
      Movie movie = movieRepository.findById(movieId);
      if (movie != null) {
        result.add(movie);
      }
    }
    result.sort(Comparator.comparing(Movie::getTitle));
    return Collections.unmodifiableList(result);
  }

  public List<Theater> viewTheatersInCity(String city) {
    return Collections.unmodifiableList(theaterRepository.findByCity(city));
  }

  public List<Show> viewShowsForMovie(String movieId) {
    return Collections.unmodifiableList(showRepository.findByMovieId(movieId));
  }

  public List<Show> viewShowsForTheater(String theaterId) {
    return Collections.unmodifiableList(showRepository.findByTheaterId(theaterId));
  }

  public List<SeatView> viewSeatMap(String showId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    return seatInventoryRepository.getSeatMap(showId, now);
  }

  public SeatLock lockSeats(String showId, List<String> seatIds, String userId, Instant now) {
    if (now == null) {
      throw new IllegalArgumentException("now");
    }
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    if (seatIds == null || seatIds.isEmpty()) {
      throw new IllegalArgumentException("seatIds");
    }
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("userId");
    }

    Show show = showRepository.findById(showId);
    if (show == null) {
      throw new IllegalArgumentException("Unknown show: " + showId);
    }

    String lockId = UUID.randomUUID().toString();
    Instant expiresAt = now.plusSeconds(DEFAULT_LOCK_TTL_SECONDS);

    long totalPriceCents = seatPricingCalculator.calculateTotalPriceCents(show, seatIds, now);
    seatInventoryRepository.lockSeats(
        showId, seatIds, lockId, userId, now, DEFAULT_LOCK_TTL_SECONDS);

    SeatLock seatLock =
        new SeatLock(lockId, showId, userId, seatIds, now, expiresAt, totalPriceCents);
    seatLockRepository.save(seatLock);
    return seatLock;
  }

  public Booking bookAndPay(String lockId, String paymentMethod, Instant now) {
    if (lockId == null || lockId.isBlank()) {
      throw new IllegalArgumentException("lockId");
    }
    if (paymentMethod == null || paymentMethod.isBlank()) {
      throw new IllegalArgumentException("paymentMethod");
    }
    if (now == null) {
      throw new IllegalArgumentException("now");
    }

    SeatLock seatLock = seatLockRepository.findById(lockId);
    if (seatLock == null) {
      throw new IllegalArgumentException("Unknown lock: " + lockId);
    }
    if (!seatLock.getExpiresAt().isAfter(now)) {
      seatLockRepository.remove(lockId);
      seatInventoryRepository.releaseLock(seatLock.getShowId(), seatLock.getSeatIds(), lockId, now);
      throw new IllegalStateException("Lock expired");
    }

    Show show = showRepository.findById(seatLock.getShowId());
    if (show == null) {
      throw new IllegalStateException("Show not found for lock: " + lockId);
    }

    long amountCents = seatLock.getTotalPriceCents();

    String bookingId = UUID.randomUUID().toString();
    Booking booking = new Booking(bookingId, seatLock.getShowId(), seatLock.getSeatIds(), amountCents, now);

    String paymentId = UUID.randomUUID().toString();
    Payment payment =
        new Payment(paymentId, bookingId, amountCents, PaymentStatus.INITIATED, now);
    paymentRepository.save(payment);

    boolean success = paymentGateway.pay(paymentId, amountCents, paymentMethod);
    payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
    paymentRepository.save(payment);

    if (!success) {
      seatInventoryRepository.releaseLock(seatLock.getShowId(), seatLock.getSeatIds(), lockId, now);
      booking.setStatus(BookingStatus.FAILED);
      bookingRepository.save(booking);
      seatLockRepository.remove(lockId);
      throw new IllegalStateException("Payment failed");
    }

    booking.setPaymentId(paymentId);
    booking.setStatus(BookingStatus.CONFIRMED);

    try {
      seatInventoryRepository.confirmLock(
          seatLock.getShowId(), seatLock.getSeatIds(), lockId, bookingId, now);
    } catch (RuntimeException e) {
      seatInventoryRepository.releaseLock(seatLock.getShowId(), seatLock.getSeatIds(), lockId, now);
      booking.setStatus(BookingStatus.FAILED);
      bookingRepository.save(booking);
      seatLockRepository.remove(lockId);
      throw e;
    }

    bookingRepository.save(booking);
    seatLockRepository.remove(lockId);
    return booking;
  }

  public Booking cancelBooking(String bookingId, Instant now) {
    if (bookingId == null || bookingId.isBlank()) {
      throw new IllegalArgumentException("bookingId");
    }
    if (now == null) {
      throw new IllegalArgumentException("now");
    }

    Booking booking = bookingRepository.findById(bookingId);
    if (booking == null) {
      throw new IllegalArgumentException("Unknown booking: " + bookingId);
    }
    if (booking.getStatus() != BookingStatus.CONFIRMED) {
      throw new IllegalStateException("Booking is not confirmed: " + bookingId);
    }

    Show show = showRepository.findById(booking.getShowId());
    if (show == null) {
      throw new IllegalStateException("Show not found: " + booking.getShowId());
    }
    if (!now.isBefore(show.getStartTime())) {
      throw new IllegalStateException("Cannot cancel after show start time");
    }

    seatInventoryRepository.cancelBookingSeats(
        booking.getShowId(), booking.getSeatIds(), bookingId, now);

    booking.setStatus(BookingStatus.CANCELLED);
    bookingRepository.save(booking);

    if (booking.getPaymentId() != null) {
      Payment payment = paymentRepository.findById(booking.getPaymentId());
      if (payment != null) {
        paymentGateway.refund(payment.getPaymentId(), payment.getAmountCents());
      }
    }

    return booking;
  }

  public void upsertMovie(String movieId, String title) {
    Movie movie = new Movie(movieId, title);
    movieRepository.upsert(movie);
  }

  public void upsertTheater(String theaterId, String name, String city) {
    Theater theater = new Theater(theaterId, name, city);
    theaterRepository.upsert(theater);
  }

  public void upsertScreen(String screenId, String theaterId, String name) {
    Screen screen = new Screen(screenId, theaterId, name);
    screenRepository.upsert(screen);
  }

  public void upsertShow(Show show) {
    if (show == null) {
      throw new IllegalArgumentException("show");
    }
    showRepository.upsert(show);
    seatInventoryRepository.initializeShow(show);
  }

  public void updatePricingPolicy(String showId, PricingPolicy pricingPolicy) {
    if (showId == null || showId.isBlank()) {
      throw new IllegalArgumentException("showId");
    }
    if (pricingPolicy == null) {
      throw new IllegalArgumentException("pricingPolicy");
    }

    Show old = showRepository.findById(showId);
    if (old == null) {
      throw new IllegalArgumentException("Unknown show: " + showId);
    }

    Show updated =
        new Show(
            old.getShowId(),
            old.getMovieId(),
            old.getTheaterId(),
            old.getScreenId(),
            old.getStartTime(),
            old.getSeatCategoryBySeatId(),
            pricingPolicy);

    showRepository.upsert(updated);
    seatInventoryRepository.initializeShow(updated);
  }
}

