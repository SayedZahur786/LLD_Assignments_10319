package com.example.movieticket.domain;

public class Movie {
  private final String movieId;
  private final String title;

  public Movie(String movieId, String title) {
    if (movieId == null || movieId.isBlank()) {
      throw new IllegalArgumentException("movieId");
    }
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("title");
    }
    this.movieId = movieId;
    this.title = title;
  }

  public String getMovieId() {
    return movieId;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public String toString() {
    return "Movie{movieId='" + movieId + "', title='" + title + "'}";
  }
}

