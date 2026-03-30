package com.example.parkinglot.repository;

import com.example.parkinglot.domain.ParkingTicket;
import java.util.*;

public class InMemoryParkingTicketRepository implements ParkingTicketRepository {
  private final Map<String, ParkingTicket> activeTicketsById = new HashMap<>();

  @Override
  public void save(ParkingTicket ticket) {
    if (ticket == null) {
      throw new IllegalArgumentException("ticket");
    }
    activeTicketsById.put(ticket.getTicketId(), ticket);
  }

  @Override
  public ParkingTicket removeActive(String ticketId) {
    ParkingTicket removed = activeTicketsById.remove(ticketId);
    if (removed == null) {
      throw new IllegalArgumentException("Invalid or already-closed ticket: " + ticketId);
    }
    return removed;
  }
}
