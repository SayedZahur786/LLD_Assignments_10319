package com.example.movieticket.repository;

import com.example.movieticket.domain.Screen;
import java.util.*;

public class InMemoryScreenRepository implements ScreenRepository {
  private final Map<String, Screen> screensById = new HashMap<>();

  @Override
  public void upsert(Screen screen) {
    if (screen == null) {
      throw new IllegalArgumentException("screen");
    }
    screensById.put(screen.getScreenId(), screen);
  }

  @Override
  public Screen findById(String screenId) {
    if (screenId == null || screenId.isBlank()) {
      throw new IllegalArgumentException("screenId");
    }
    return screensById.get(screenId);
  }

  @Override
  public List<Screen> findByTheaterId(String theaterId) {
    if (theaterId == null || theaterId.isBlank()) {
      throw new IllegalArgumentException("theaterId");
    }
    List<Screen> result = new ArrayList<>();
    for (Screen s : screensById.values()) {
      if (theaterId.equals(s.getTheaterId())) {
        result.add(s);
      }
    }
    return result;
  }

  @Override
  public Collection<Screen> findAll() {
    return Collections.unmodifiableCollection(screensById.values());
  }
}

