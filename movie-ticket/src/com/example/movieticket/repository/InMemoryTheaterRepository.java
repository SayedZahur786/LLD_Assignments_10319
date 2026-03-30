package com.example.movieticket.repository;

import com.example.movieticket.domain.Theater;
import java.util.*;

public class InMemoryTheaterRepository implements TheaterRepository {
  private final Map<String, Theater> theatersById = new HashMap<>();

  @Override
  public void upsert(Theater theater) {
    if (theater == null) {
      throw new IllegalArgumentException("theater");
    }
    theatersById.put(theater.getTheaterId(), theater);
  }

  @Override
  public Theater findById(String theaterId) {
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    return theatersById.get(theaterId);
  }

  @Override
  public List<Theater> findByCity(String city) {
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("city");
    }
    List<Theater> result = new ArrayList<>();
    for (Theater t : theatersById.values()) {
      if (city.equals(t.getCity())) {
        result.add(t);
      }
    }
    return result;
  }

  @Override
  public Collection<Theater> findAll() {
    return Collections.unmodifiableCollection(theatersById.values());
  }
}

