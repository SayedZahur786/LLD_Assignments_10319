package com.example.parkinglot.repository;

import com.example.parkinglot.domain.ParkingTicket;

public interface ParkingTicketRepository {
  void save(ParkingTicket ticket);

  ParkingTicket removeActive(String ticketId);
}
