package com.railwayreservationsystem.reservation.service.impl;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.model.Reservation;
import com.railwayreservationsystem.reservation.model.ReservationRequest;
import com.railwayreservationsystem.reservation.model.Train;
import com.railwayreservationsystem.reservation.service.ReservationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("com.railwayreservationsystem.reservation.service.impl.ReservationServiceImpl")
public class ReservationServiceImpl implements ReservationService {
  private Train train;
  private List<Reservation> reservations = new ArrayList<>();

  private Map<String, Integer> sectionAUserSeatMap = new HashMap<>();
  private Map<String, Integer> sectionBUserSeatMap = new HashMap<>();

  private Long nextReservationId = 1L;

  private static final String SECTION_A = "A";
  private static final String SECTION_B = "B";
  private static final int MAX_SEATS_IN_SECTION = 50;

  private static final Double PRICE_PER_TICKET = 20.0;

  @PostConstruct
  // Initialize train data assuming max seats per section = 50.
  public void init() {
    train = Train.builder().origin("London").destination("France").ticketPrice(PRICE_PER_TICKET)
        .availableSeatsSectionA(MAX_SEATS_IN_SECTION).availableSeatsSectionB(MAX_SEATS_IN_SECTION)
        .build();
  }

  @Override
  public Receipt makeReservation(ReservationRequest reservationRequest) {
    Reservation reservation =
        Reservation.builder().id(nextReservationId++).user(reservationRequest.getUser())
            .train(train).section(reservationRequest.getSection()).build();

    synchronized (this) {
      if (isSeatNumberTaken(reservationRequest.getSeatNumber(), reservationRequest.getSection())) {
        log.error("Seat already taken");
        return null;
      }

      if (SECTION_A.equalsIgnoreCase(reservation.getSection())) {
        if (train.getAvailableSeatsSectionA() > 0) {
          train.setAvailableSeatsSectionA(train.getAvailableSeatsSectionA() - 1);
          sectionAUserSeatMap.put(reservationRequest.getUser().getEmail(),
              reservationRequest.getSeatNumber());

          // We can also modify this to have a random seat number if not to be taken as an input
          // while booking.

          reservation.setSeatNumber(
              sectionAUserSeatMap.get(reservationRequest.getUser().getEmail()));
        } else {
          log.error("Reservation failed: No seats available in section: {}",
              reservationRequest.getSection());
          return null;
        }
      } else if (SECTION_B.equalsIgnoreCase(reservation.getSection())) {
        if (train.getAvailableSeatsSectionB() > 0) {
          train.setAvailableSeatsSectionB(train.getAvailableSeatsSectionB() - 1);
          sectionBUserSeatMap.put(reservationRequest.getUser().getEmail(),
              reservationRequest.getSeatNumber());

          reservation.setSeatNumber(
              sectionBUserSeatMap.get(reservationRequest.getUser().getEmail()));
        } else {
          log.error("Reservation failed: No seats available in section: {}",
              reservationRequest.getSection());
          return null;
        }
      }
      reservations.add(reservation);
      return getReservationReceipt(reservation);
    }
  }

  private Receipt getReservationReceipt(Reservation reservation) {
    return Receipt.builder().user(reservation.getUser())
        .pricePaid(reservation.getTrain().getTicketPrice())
        .startPoint(reservation.getTrain().getOrigin())
        .destination(reservation.getTrain().getDestination()).build();
  }

  @Override
  public List<Reservation> getReservationsBySection(String section) {
    List<Reservation> sectionReservations = new ArrayList<>();
    for (Reservation reservation : reservations) {
      if (reservation.getSection().equalsIgnoreCase(section)) {
        sectionReservations.add(reservation);
      }
    }
    if (!CollectionUtils.isEmpty(sectionReservations)) {
      sectionReservations = sectionReservations.stream().map(this::modifyReservationsData)
          .collect(Collectors.toList());
    }
    return sectionReservations;
  }

  @Override
  public Receipt getUserReservationReceipt(String email) {
    Optional<Reservation> userReservation = reservations.stream()
        .filter(reservation -> reservation.getUser().getEmail().equalsIgnoreCase(email))
        .findFirst();
    return userReservation.map(this::getReservationReceipt).orElse(null);
  }

  private Reservation modifyReservationsData(Reservation sectionReservation) {
    return Reservation.builder().user(sectionReservation.getUser())
        .section(sectionReservation.getSection()).seatNumber(sectionReservation.getSeatNumber())
        .build();
  }

  @Override
  public Boolean removeUserFromReservation(String email) {
    log.info("Request to remove reservation for user with email: {}", email);
    synchronized (this) {
      for (Reservation reservation : reservations) {
        if (reservation.getUser().getEmail().equalsIgnoreCase(email)) {
          reservations.remove(reservation);
          if (SECTION_A.equalsIgnoreCase(reservation.getSection())) {
            train.setAvailableSeatsSectionA(train.getAvailableSeatsSectionA() + 1);
            sectionAUserSeatMap.remove(email);
          } else if (SECTION_B.equalsIgnoreCase(reservation.getSection())) {
            train.setAvailableSeatsSectionB(train.getAvailableSeatsSectionB() + 1);
            sectionBUserSeatMap.remove(email);
          }
          return Boolean.TRUE;
        } else {
          log.error("No reservation found in system for user with email: {}", email);
        }
      }
      return Boolean.FALSE;
    }
  }

  @Override
  public Boolean modifyUserSeat(String email, String newSection, int newSeatNumber) {
    synchronized (this) {
      for (Reservation reservation : reservations) {
        if (reservation.getUser().getEmail().equals(email)) {
          if (isSeatNumberTaken(newSeatNumber, newSection)) {
            log.error("Seat already taken");
            return Boolean.FALSE;
          }
          if (SECTION_A.equalsIgnoreCase(reservation.getSection())) {
            train.setAvailableSeatsSectionA(train.getAvailableSeatsSectionA() + 1);
            sectionAUserSeatMap.remove(email);
          } else if (SECTION_B.equalsIgnoreCase(reservation.getSection())) {
            train.setAvailableSeatsSectionB(train.getAvailableSeatsSectionB() + 1);
            sectionBUserSeatMap.remove(email);
          }

          if (SECTION_A.equalsIgnoreCase(newSection)) {
            train.setAvailableSeatsSectionA(train.getAvailableSeatsSectionA() - 1);
            sectionAUserSeatMap.put(email, newSeatNumber);

          } else if (SECTION_B.equalsIgnoreCase(newSection)) {
            train.setAvailableSeatsSectionB(train.getAvailableSeatsSectionB() - 1);
            sectionBUserSeatMap.put(email, newSeatNumber);
          }
          reservation.setSection(newSection);
          reservation.setSeatNumber(newSeatNumber);
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }
  }

  private boolean isSeatNumberTaken(int seatNumber, String section) {
    if (SECTION_A.equalsIgnoreCase(section)) {
      return sectionAUserSeatMap.containsValue(seatNumber);
    } else if (SECTION_B.equalsIgnoreCase(section)) {
      return sectionBUserSeatMap.containsValue(seatNumber);
    }
    return false;
  }
}
