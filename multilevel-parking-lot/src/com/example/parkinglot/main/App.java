package com.example.parkinglot.main;

import com.example.parkinglot.adapter.GateSlotIndex;
import com.example.parkinglot.adapter.NearestAvailableSlotStrategy;
import com.example.parkinglot.controller.ParkingLotController;
import com.example.parkinglot.domain.Bill;
import com.example.parkinglot.domain.ParkingSlot;
import com.example.parkinglot.domain.ParkingTicket;
import com.example.parkinglot.domain.RateCard;
import com.example.parkinglot.domain.SlotType;
import com.example.parkinglot.domain.Vehicle;
import com.example.parkinglot.domain.VehicleType;
import com.example.parkinglot.repository.InMemoryParkingSlotRepository;
import com.example.parkinglot.repository.InMemoryParkingTicketRepository;
import com.example.parkinglot.service.ParkingLotService;
import java.time.Instant;
import java.util.*;

public class App {
  public static void main(String[] args) {
    Map<String, ParkingSlot> slotsById = new HashMap<>();
    slotsById.put("F1-S1", new ParkingSlot("F1-S1", SlotType.SMALL, 1));
    slotsById.put("F1-S2", new ParkingSlot("F1-S2", SlotType.MEDIUM, 1));
    slotsById.put("F1-S3", new ParkingSlot("F1-S3", SlotType.LARGE, 1));
    slotsById.put("F2-S1", new ParkingSlot("F2-S1", SlotType.SMALL, 2));
    slotsById.put("F2-S2", new ParkingSlot("F2-S2", SlotType.MEDIUM, 2));
    slotsById.put("F2-S3", new ParkingSlot("F2-S3", SlotType.LARGE, 2));

    Map<String, List<String>> gateOrder = new HashMap<>();
    gateOrder.put("GATE-A", List.of("F1-S1", "F1-S2", "F1-S3", "F2-S1", "F2-S2", "F2-S3"));
    gateOrder.put("GATE-B", List.of("F2-S3", "F2-S2", "F2-S1", "F1-S3", "F1-S2", "F1-S1"));

    Map<SlotType, Integer> hourlyRateCents = new HashMap<>();
    hourlyRateCents.put(SlotType.SMALL, 1000);
    hourlyRateCents.put(SlotType.MEDIUM, 2000);
    hourlyRateCents.put(SlotType.LARGE, 3500);

    InMemoryParkingSlotRepository slotRepository = new InMemoryParkingSlotRepository(slotsById);
    InMemoryParkingTicketRepository ticketRepository = new InMemoryParkingTicketRepository();
    GateSlotIndex gateSlotIndex = new GateSlotIndex(gateOrder);
    NearestAvailableSlotStrategy strategy =
        new NearestAvailableSlotStrategy(slotRepository, gateSlotIndex);
    RateCard rateCard = new RateCard(hourlyRateCents);
    ParkingLotService parkingLot =
        new ParkingLotService(slotRepository, ticketRepository, rateCard, strategy);
    ParkingLotController controller = new ParkingLotController(parkingLot);

    Instant t0 = Instant.parse("2026-03-27T10:00:00Z");
    ParkingTicket bikeTicket =
        controller.park(
            new Vehicle("WB-02-AB-1111", VehicleType.TWO_WHEELER), t0, SlotType.SMALL, "GATE-A");
    ParkingTicket carTicket =
        controller.park(
            new Vehicle("WB-02-CD-2222", VehicleType.CAR),
            t0.plusSeconds(300),
            SlotType.MEDIUM,
            "GATE-A");
    ParkingTicket busTicket =
        controller.park(
            new Vehicle("WB-02-EF-3333", VehicleType.BUS),
            t0.plusSeconds(600),
            SlotType.LARGE,
            "GATE-B");

    System.out.println(bikeTicket);
    System.out.println(carTicket);
    System.out.println(busTicket);
    System.out.println(controller.status());

    Bill bikeBill = controller.exit(bikeTicket, t0.plusSeconds(70 * 60));
    Bill carBill = controller.exit(carTicket, t0.plusSeconds(130 * 60));
    Bill busBill = controller.exit(busTicket, t0.plusSeconds(40 * 60));

    System.out.println(bikeBill);
    System.out.println(carBill);
    System.out.println(busBill);
    System.out.println(controller.status());
  }
}
