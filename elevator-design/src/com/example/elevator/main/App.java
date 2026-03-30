package com.example.elevator.main;

import com.example.elevator.adapter.AllowlistExpressStopPolicy;
import com.example.elevator.adapter.ConsoleAnnouncementService;
import com.example.elevator.adapter.ConsoleSoundNotifier;
import com.example.elevator.adapter.LookDispatchStrategy;
import com.example.elevator.controller.ElevatorController;
import com.example.elevator.domain.BuildingStatus;
import com.example.elevator.domain.CallPriority;
import com.example.elevator.domain.Direction;
import com.example.elevator.domain.DispatchResult;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorCarSpec;
import com.example.elevator.domain.EmergencyType;
import com.example.elevator.repository.InMemoryElevatorCarRepository;
import com.example.elevator.repository.InMemoryHallCallRepository;
import com.example.elevator.service.ElevatorSystemService;
import java.util.*;

public class App {
  public static void main(String[] args) throws InterruptedException {
    ElevatorCarSpec e1Spec = new ElevatorCarSpec("E1", 700, false, Set.of());
    ElevatorCarSpec e2Spec = new ElevatorCarSpec("E2", 700, true, Set.of(1, 3, 5, 7, 9));

    ElevatorCar e1 = new ElevatorCar(e1Spec, 1);
    ElevatorCar e2 = new ElevatorCar(e2Spec, 1);

    InMemoryElevatorCarRepository elevatorRepo = new InMemoryElevatorCarRepository(List.of(e1, e2));
    InMemoryHallCallRepository hallCallRepo = new InMemoryHallCallRepository();
    LookDispatchStrategy dispatchStrategy = new LookDispatchStrategy();
    AllowlistExpressStopPolicy expressStopPolicy = new AllowlistExpressStopPolicy();
    ConsoleAnnouncementService announcementService = new ConsoleAnnouncementService();
    ConsoleSoundNotifier soundNotifier = new ConsoleSoundNotifier();

    ElevatorSystemService service =
        new ElevatorSystemService(
            elevatorRepo,
            hallCallRepo,
            dispatchStrategy,
            expressStopPolicy,
            announcementService,
            soundNotifier);

    ElevatorController controller = new ElevatorController(service);

    for (int f = 1; f <= 10; f++) {
      controller.addFloor(f);
    }
    controller.setPriorityFloor(8, true);

    System.out.println(
        "Initial status: " + controller.status().getElevators().size() + " elevators");

    DispatchResult r1 = controller.pressOutsideHallButton(5, Direction.UP, Locale.ENGLISH);
    System.out.println("Dispatch result: " + r1);
    for (int i = 0; i < 8; i++) {
      controller.tickAllElevators();
    }
    controller.pressDoorCloseButton(r1.getElevatorId());

    DispatchResult r2 = controller.pressOutsideHallButton(7, Direction.UP, Locale.ENGLISH);
    System.out.println("Dispatch result: " + r2);
    for (int i = 0; i < 2; i++) controller.tickAllElevators();

    controller.updateElevatorLoadKg(r2.getElevatorId(), 800, Locale.ENGLISH);
    for (int i = 0; i < 5; i++) controller.tickAllElevators();

    controller.updateElevatorLoadKg(r2.getElevatorId(), 650, Locale.ENGLISH);
    controller.pressDoorCloseButton(r2.getElevatorId());
    for (int i = 0; i < 10; i++) controller.tickAllElevators();

    DispatchResult r3 =
        controller.pressOutsideHallButton(6, Direction.UP, CallPriority.NORMAL, Locale.ENGLISH);
    System.out.println("Dispatch result (express test): " + r3);

    controller.triggerEmergency(EmergencyType.POWER_OUTAGE, Locale.ENGLISH);
    System.out.println("After emergency: " + controller.status().isEmergencyActive());
    DispatchResult r4 = controller.pressOutsideHallButton(4, Direction.DOWN, Locale.ENGLISH);
    System.out.println("Dispatch result during emergency (expected rejected): " + r4);
    controller.tickAllElevators();

    controller.clearEmergency();
    controller.tickAllElevators();

    Thread tUp =
        new Thread(
            () -> {
              DispatchResult rr =
                  controller.pressOutsideHallButton(3, Direction.UP, Locale.ENGLISH);
              System.out.println("Thread-UP result: " + rr);
            });
    Thread tDown =
        new Thread(
            () -> {
              DispatchResult rr =
                  controller.pressOutsideHallButton(3, Direction.DOWN, Locale.ENGLISH);
              System.out.println("Thread-DOWN result: " + rr);
            });

    tUp.start();
    tDown.start();
    tUp.join();
    tDown.join();

    for (int i = 0; i < 6; i++) controller.tickAllElevators();

    BuildingStatus status = controller.status();
    System.out.println("Final emergencyActive: " + status.isEmergencyActive());
    System.out.println("Final elevators: " + status.getElevators().size());
  }
}
