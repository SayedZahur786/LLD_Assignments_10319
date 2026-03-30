package com.example.movieticket.main;

import com.example.movieticket.adapter.*;
import com.example.movieticket.controller.*;
import com.example.movieticket.domain.*;
import com.example.movieticket.repository.*;
import com.example.movieticket.service.MovieTicketService;
import java.time.Instant;
import java.util.*;

public class App {
  public static void main(String[] args) {
    MovieRepository movieRepository = new InMemoryMovieRepository();
    TheaterRepository theaterRepository = new InMemoryTheaterRepository();
    ScreenRepository screenRepository = new InMemoryScreenRepository();
    ShowRepository showRepository = new InMemoryShowRepository();

    SeatInventoryRepository seatInventoryRepository = new InMemorySeatInventoryRepository();
    SeatLockRepository seatLockRepository = new InMemorySeatLockRepository();
    BookingRepository bookingRepository = new InMemoryBookingRepository();
    PaymentRepository paymentRepository = new InMemoryPaymentRepository();

    PaymentGateway paymentGateway = new FakePaymentGateway(true);
    SeatPricingCalculator seatPricingCalculator = new SeatPricingCalculator(seatInventoryRepository);

    MovieTicketService service =
        new MovieTicketService(
            movieRepository,
            theaterRepository,
            screenRepository,
            showRepository,
            seatInventoryRepository,
            seatLockRepository,
            bookingRepository,
            paymentRepository,
            paymentGateway,
            seatPricingCalculator);

    AdminController admin = new AdminController(service);
    BookingController user = new BookingController(service);

    String movieId = "M1";
    String theaterId = "T1";
    String screenId = "S1";
    String showId = "SHOW-1";

    admin.upsertMovie(movieId, "Example Movie");
    admin.upsertTheater(theaterId, "City Cinema", "Pune");
    admin.upsertScreen(screenId, theaterId, "Screen 1");

    Map<SeatCategory, Long> base = new EnumMap<>(SeatCategory.class);
    base.put(SeatCategory.SILVER, 500);
    base.put(SeatCategory.GOLD, 850);
    base.put(SeatCategory.PLATINUM, 1200);

    PricingPolicy pricingPolicy =
        new PricingPolicy(
            base,
            18,
            21,
            1.4,
            1.0,
            1.2,
            1.0,
            1.2,
            1.5);

    Map<String, SeatCategory> seatLayout = new LinkedHashMap<>();
    seatLayout.put("A1", SeatCategory.SILVER);
    seatLayout.put("A2", SeatCategory.SILVER);
    seatLayout.put("A3", SeatCategory.GOLD);
    seatLayout.put("A4", SeatCategory.GOLD);
    seatLayout.put("B1", SeatCategory.SILVER);
    seatLayout.put("B2", SeatCategory.GOLD);
    seatLayout.put("B3", SeatCategory.PLATINUM);
    seatLayout.put("B4", SeatCategory.PLATINUM);

    Instant now = Instant.parse("2026-03-27T10:00:00Z");
    Instant startTime = now.plusSeconds(3600);

    Show show =
        new Show(
            showId, movieId, theaterId, screenId, startTime, seatLayout, pricingPolicy);
    admin.upsertShow(show);

    List<String> seatIds = List.of("A3", "A4");
    SeatLock lock = user.lockSeats(showId, seatIds, "USER-1", now);
    Booking booking = user.bookAndPay(lock.getLockId(), "CARD", now.plusSeconds(10));
    System.out.println(booking);

    Booking cancelled = user.cancelBooking(booking.getBookingId(), startTime.minusSeconds(60));
    System.out.println(cancelled);
  }
}

