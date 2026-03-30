package com.example.movieticket.repository;

import com.example.movieticket.domain.Screen;
import java.util.*;

public interface ScreenRepository {
  void upsert(Screen screen);

  Screen findById(String screenId);

  List<Screen> findByTheaterId(String theaterId);

  Collection<Screen> findAll();
}

