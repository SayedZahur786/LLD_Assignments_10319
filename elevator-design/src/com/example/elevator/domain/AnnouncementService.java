package com.example.elevator.domain;

import java.util.*;

public interface AnnouncementService {
  void announce(AnnouncementKey key, Locale locale, Map<String, String> params);
}
