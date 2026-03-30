package com.example.movieticket.controller;

import com.example.movieticket.domain.PricingPolicy;
import com.example.movieticket.domain.Show;
import com.example.movieticket.service.MovieTicketService;

public class AdminController {
  private final MovieTicketService movieTicketService;

  public AdminController(MovieTicketService movieTicketService) {
    if (movieTicketService == null) {
      throw new IllegalArgumentException("movieTicketService");
    }
    this.movieTicketService = movieTicketService;
  }

  public void upsertMovie(String movieId, String title) {
    movieTicketService.upsertMovie(movieId, title);
  }

  public void upsertTheater(String theaterId, String name, String city) {
    movieTicketService.upsertTheater(theaterId, name, city);
  }

  public void upsertScreen(String screenId, String theaterId, String name) {
    movieTicketService.upsertScreen(screenId, theaterId, name);
  }

  public void upsertShow(Show show) {
    movieTicketService.upsertShow(show);
  }

  public void updatePricingPolicy(String showId, PricingPolicy pricingPolicy) {
    movieTicketService.updatePricingPolicy(showId, pricingPolicy);
  }
}

