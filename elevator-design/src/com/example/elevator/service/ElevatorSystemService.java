package com.example.elevator.service;

import com.example.elevator.domain.AnnouncementKey;
import com.example.elevator.domain.AnnouncementService;
import com.example.elevator.domain.BuildingStatus;
import com.example.elevator.domain.CallPriority;
import com.example.elevator.domain.Direction;
import com.example.elevator.domain.DispatchResult;
import com.example.elevator.domain.ElevatorCar;
import com.example.elevator.domain.ElevatorCarSpec;
import com.example.elevator.domain.ElevatorDispatchStrategy;
import com.example.elevator.domain.ElevatorState;
import com.example.elevator.domain.ElevatorStatus;
import com.example.elevator.domain.EmergencyType;
import com.example.elevator.domain.ExpressStopPolicy;
import com.example.elevator.domain.HallCall;
import com.example.elevator.domain.SoundNotifier;
import com.example.elevator.repository.ElevatorCarRepository;
import com.example.elevator.repository.HallCallRepository;
import java.util.*;

public class ElevatorSystemService {
  private final ElevatorCarRepository elevatorCarRepository;
  private final HallCallRepository hallCallRepository;
  private final ElevatorDispatchStrategy dispatchStrategy;
  private final ExpressStopPolicy expressStopPolicy;
  private final AnnouncementService announcementService;
  private final SoundNotifier soundNotifier;
  private final Object hallDispatchLock = new Object();

  private final Map<String, Object> elevatorLocks = new HashMap<>();

  private final Set<Integer> enabledFloors = new HashSet<>();
  private final Set<Integer> disabledFloors = new HashSet<>();

  private final Set<Integer> priorityFloors = new HashSet<>();

  private final Set<Integer> disabledHallUpFloors = new HashSet<>();
  private final Set<Integer> disabledHallDownFloors = new HashSet<>();

  private final Map<String, Set<Integer>> disabledInCarButtonsByElevator = new HashMap<>();

  private long callSequence = 1;

  private final EmergencyController emergencyController;
  private final WeightLimitController weightLimitController;
  private final MaintenanceController maintenanceController;
  private final MotionSimulator motionSimulator;

  public ElevatorSystemService(
      ElevatorCarRepository elevatorCarRepository,
      HallCallRepository hallCallRepository,
      ElevatorDispatchStrategy dispatchStrategy,
      ExpressStopPolicy expressStopPolicy,
      AnnouncementService announcementService,
      SoundNotifier soundNotifier) {
    if (elevatorCarRepository == null
        || hallCallRepository == null
        || dispatchStrategy == null
        || expressStopPolicy == null
        || announcementService == null
        || soundNotifier == null) {
      throw new IllegalArgumentException("dependencies must not be null");
    }
    this.elevatorCarRepository = elevatorCarRepository;
    this.hallCallRepository = hallCallRepository;
    this.dispatchStrategy = dispatchStrategy;
    this.expressStopPolicy = expressStopPolicy;
    this.announcementService = announcementService;
    this.soundNotifier = soundNotifier;

    for (ElevatorCar car : elevatorCarRepository.findAll()) {
      elevatorLocks.put(car.getElevatorId(), new Object());
    }

    this.emergencyController =
        new EmergencyController(
            hallDispatchLock,
            elevatorLocks,
            elevatorCarRepository,
            announcementService,
            soundNotifier);
    this.weightLimitController =
        new WeightLimitController(
            elevatorLocks, elevatorCarRepository, announcementService, soundNotifier);
    this.maintenanceController =
        new MaintenanceController(
            hallDispatchLock,
            elevatorLocks,
            elevatorCarRepository,
            hallCallRepository,
            dispatchStrategy,
            expressStopPolicy);
    this.motionSimulator =
        new MotionSimulator(elevatorLocks, elevatorCarRepository, hallCallRepository, announcementService);
  }

  public void addFloor(int floor) {
    if (floor < 0) {
      throw new IllegalArgumentException("floor");
    }
    synchronized (hallDispatchLock) {
      enabledFloors.add(floor);
    }
  }

  public void setFloorEnabled(int floor, boolean enabled) {
    if (floor < 0) {
      throw new IllegalArgumentException("floor");
    }
    synchronized (hallDispatchLock) {
      if (enabled) {
        enabledFloors.add(floor);
        disabledFloors.remove(floor);
      } else {
        disabledFloors.add(floor);
      }
    }
  }

  public void setPriorityFloor(int floor, boolean prioritized) {
    if (floor < 0) {
      throw new IllegalArgumentException("floor");
    }
    synchronized (hallDispatchLock) {
      if (prioritized) priorityFloors.add(floor);
      else priorityFloors.remove(floor);
    }
  }

  public void setHallButtonEnabled(int floor, Direction direction, boolean enabled) {
    if (direction == null) throw new IllegalArgumentException("direction");
    if (floor < 0) throw new IllegalArgumentException("floor");
    synchronized (hallDispatchLock) {
      if (enabled) {
        if (direction == Direction.UP) disabledHallUpFloors.remove(floor);
        if (direction == Direction.DOWN) disabledHallDownFloors.remove(floor);
      } else {
        if (direction == Direction.UP) disabledHallUpFloors.add(floor);
        if (direction == Direction.DOWN) disabledHallDownFloors.add(floor);
      }
    }
  }

  public void disableInCarButton(String elevatorId, int floor, boolean disabled) {
    if (elevatorId == null || elevatorId.isBlank()) {
      throw new IllegalArgumentException("elevatorId");
    }
    if (floor < 0) throw new IllegalArgumentException("floor");
    synchronized (hallDispatchLock) {
      Set<Integer> set = disabledInCarButtonsByElevator.get(elevatorId);
      if (set == null) {
        set = new HashSet<>();
        disabledInCarButtonsByElevator.put(elevatorId, set);
      }
      if (disabled) set.add(floor);
      else set.remove(floor);
    }
  }

  public void addElevatorCar(ElevatorCarSpec spec, int startingFloor) {
    if (spec == null) {
      throw new IllegalArgumentException("spec");
    }
    ElevatorCar car = new ElevatorCar(spec, startingFloor);
    elevatorCarRepository.save(car);
    elevatorLocks.put(spec.getElevatorId(), new Object());
  }

  public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
    maintenanceController.setElevatorMaintenance(elevatorId, maintenance);
  }

  public void triggerEmergency(EmergencyType type, Locale locale) {
    emergencyController.triggerEmergency(type, locale);
  }

  public void clearEmergency() {
    emergencyController.clearEmergency();
  }

  public DispatchResult pressHallButton(int floor, Direction direction, Locale locale) {
    CallPriority priority =
        priorityFloors.contains(floor) ? CallPriority.PRIORITIZED : CallPriority.NORMAL;
    return pressHallButton(floor, direction, priority, locale);
  }

  public DispatchResult pressHallButton(
      int floor, Direction direction, CallPriority priority, Locale locale) {
    if (direction == null) throw new IllegalArgumentException("direction");
    if (floor < 0) throw new IllegalArgumentException("floor");
    if (priority == null) throw new IllegalArgumentException("priority");

    if (emergencyController.isEmergencyActive()) {
      return DispatchResult.rejected("N/A", "Rejected: emergency active");
    }

    if (!enabledFloors.contains(floor) || disabledFloors.contains(floor)) {
      return DispatchResult.rejected("N/A", "Rejected: floor disabled");
    }

    if ((direction == Direction.UP && disabledHallUpFloors.contains(floor))
        || (direction == Direction.DOWN && disabledHallDownFloors.contains(floor))) {
      return DispatchResult.rejected("N/A", "Rejected: hall button disabled");
    }

    String hallCallId = UUID.randomUUID().toString();
    long seq;
    synchronized (hallDispatchLock) {
      seq = callSequence++;
    }
    HallCall call = new HallCall(hallCallId, floor, direction, priority, seq);
    hallCallRepository.save(call);

    String elevatorId;
    synchronized (hallDispatchLock) {
      List<ElevatorCar> candidates = new ArrayList<>(elevatorCarRepository.findAll());
      candidates.removeIf(c -> c.getState() == ElevatorState.UNDER_MAINTENANCE);

      elevatorId = dispatchStrategy.assignElevator(call, candidates, expressStopPolicy);
      if (elevatorId == null) {
        return DispatchResult.rejected(hallCallId, "Rejected: no elevator available");
      }

      Object lock = elevatorLocks.get(elevatorId);
      if (lock == null) {
        return DispatchResult.rejected(hallCallId, "Rejected: elevator lock not found");
      }
      synchronized (lock) {
        ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
        if (elevator == null) {
          return DispatchResult.rejected(hallCallId, "Rejected: elevator not found");
        }

        if (!expressStopPolicy.canStopAt(elevator.getSpec(), floor)) {
          return DispatchResult.rejected(
              hallCallId, "Rejected: express elevator cannot stop at this floor");
        }
        elevator.addStopFloor(floor);
        elevator.setDoorOpen(false);
        hallCallRepository.assignToElevator(hallCallId, elevatorId);
      }
    }

    announcementService.announce(
        com.example.elevator.domain.AnnouncementKey.ELEVATOR_ASSIGNED,
        locale,
        buildAnnouncementParams(
            locale, elevatorId, floor, call.getDirection(), call.getPriority()));

    return DispatchResult.accepted(hallCallId, elevatorId);
  }

  private Map<String, String> buildAnnouncementParams(
      Locale locale, String elevatorId, int floor, Direction direction, CallPriority priority) {
    Map<String, String> params = new HashMap<>();
    params.put("elevatorId", elevatorId);
    params.put("floor", String.valueOf(floor));
    params.put("direction", direction.toString());
    params.put("priority", priority.toString());
    return params;
  }

  public boolean pressInCarButton(String elevatorId, int floor, Locale locale) {
    if (elevatorId == null || elevatorId.isBlank()) {
      throw new IllegalArgumentException("elevatorId");
    }
    if (floor < 0) throw new IllegalArgumentException("floor");
    if (emergencyController.isEmergencyActive()) return false;
    if (!enabledFloors.contains(floor) || disabledFloors.contains(floor)) return false;

    ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
    if (elevator == null) throw new IllegalStateException("Unknown elevator: " + elevatorId);

    if (!expressStopPolicy.canStopAt(elevator.getSpec(), floor)) {
      return false;
    }

    Set<Integer> disabled = disabledInCarButtonsByElevator.get(elevatorId);
    if (disabled != null && disabled.contains(floor)) {
      return false;
    }

    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) throw new IllegalStateException("Missing lock for elevator: " + elevatorId);

    synchronized (lock) {
      elevator.addStopFloor(floor);
      return true;
    }
  }

  public boolean openDoor(String elevatorId, Locale locale) {
    if (elevatorId == null || elevatorId.isBlank())
      throw new IllegalArgumentException("elevatorId");
    if (emergencyController.isEmergencyActive()) {
      ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
      if (elevator == null) return false;
      Object lock = elevatorLocks.get(elevatorId);
      if (lock == null) return false;
      synchronized (lock) {
        elevator.setDoorOpen(true);
        return true;
      }
    }

    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) return false;
    synchronized (lock) {
      ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
      if (elevator == null) return false;
      if (elevator.getState() == ElevatorState.UNDER_MAINTENANCE) return false;
      elevator.setDoorOpen(true);
      return true;
    }
  }

  public boolean closeDoor(String elevatorId) {
    if (elevatorId == null || elevatorId.isBlank())
      throw new IllegalArgumentException("elevatorId");
    Object lock = elevatorLocks.get(elevatorId);
    if (lock == null) return false;

    synchronized (lock) {
      ElevatorCar elevator = elevatorCarRepository.findById(elevatorId);
      if (elevator == null) return false;

      if (elevator.getState() == ElevatorState.UNDER_MAINTENANCE) return false;
      if (emergencyController.isEmergencyActive()) return false;
      if (elevator.isOverweight()) return false;

      elevator.setDoorOpen(false);
      if (elevator.getState() == ElevatorState.IDLE && !elevator.isStopFloorsEmpty()) {
        int current = elevator.getCurrentFloor();
        Integer nextAbove = elevator.nextStopAbove(current);
        if (nextAbove != null) elevator.setState(ElevatorState.MOVING_UP);
        else elevator.setState(ElevatorState.MOVING_DOWN);
      }
      return true;
    }
  }

  public boolean updateElevatorLoadKg(String elevatorId, int loadKg, Locale locale) {
    if (elevatorId == null || elevatorId.isBlank())
      throw new IllegalArgumentException("elevatorId");
    if (loadKg < 0) throw new IllegalArgumentException("loadKg");
    if (emergencyController.isEmergencyActive()) return false;
    return weightLimitController.updateElevatorLoadKg(elevatorId, loadKg, locale);
  }

  public void tickAllElevators() {
    motionSimulator.tickAllElevators(emergencyController.isEmergencyActive());
  }

  public void tickElevator(String elevatorId) {
    motionSimulator.tickElevator(elevatorId, emergencyController.isEmergencyActive());
  }

  private void serveHallCallsAtFloor(String elevatorId, int floor) {
    List<HallCall> toServe =
        hallCallRepository.findUnservedAssignedToElevatorAtFloor(elevatorId, floor);
    if (toServe.isEmpty()) return;

    for (HallCall call : toServe) {
      hallCallRepository.markServed(call.getHallCallId());
      Map<String, String> params = new HashMap<>();
      params.put("elevatorId", elevatorId);
      params.put("floor", String.valueOf(floor));
      params.put("direction", call.getDirection().toString());
      params.put("priority", call.getPriority().toString());
      announcementService.announce(AnnouncementKey.ELEVATOR_ASSIGNED, Locale.ENGLISH, params);
    }
  }

  public BuildingStatus status() {
    Set<Integer> disabledFloorsSnapshot = new HashSet<>(disabledFloors);
    List<ElevatorStatus> elevatorStatuses = new ArrayList<>();
    for (ElevatorCar car : elevatorCarRepository.findAll()) {
      Object lock = elevatorLocks.get(car.getElevatorId());
      if (lock == null) continue;
      synchronized (lock) {
        Set<Integer> pending = car.stopFloorsSnapshot();
        ElevatorCarSpec spec = car.getSpec();
        elevatorStatuses.add(
            new ElevatorStatus(
                car.getElevatorId(),
                car.getState(),
                car.getCurrentFloor(),
                car.isDoorOpen(),
                car.getCurrentLoadKg(),
                spec.getCapacityKg(),
                pending,
                car.isOverweight(),
                spec.isExpressEnabled()));
      }
    }

    return new BuildingStatus(
        new HashSet<>(enabledFloors),
        disabledFloorsSnapshot,
        emergencyController.isEmergencyActive(),
        emergencyController.getEmergencyType(),
        elevatorStatuses);
  }
}
