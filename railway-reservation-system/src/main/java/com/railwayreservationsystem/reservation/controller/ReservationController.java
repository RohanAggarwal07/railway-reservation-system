package com.railwayreservationsystem.reservation.controller;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.model.Reservation;
import com.railwayreservationsystem.reservation.model.ReservationRequest;
import com.railwayreservationsystem.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/reservation")
public class ReservationController {
  @Autowired
  @Qualifier("com.railwayreservationsystem.reservation.service.impl.ReservationServiceImpl")
  private ReservationService reservationService;

  @PostMapping("/create")
  public ResponseEntity<Receipt> makeReservation(@RequestBody ReservationRequest request) {
    log.info("Request to make seat reservation for requestContext: {}", request);
    Receipt reservationReceipt = reservationService.makeReservation(request);
    if (Objects.nonNull(reservationReceipt)) {
      return new ResponseEntity<>(reservationReceipt, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/section/{section}")
  public ResponseEntity<List<Reservation>> getReservationsBySection(@PathVariable String section) {
    List<Reservation> sectionReservations = reservationService.getReservationsBySection(section);
    return new ResponseEntity<>(sectionReservations, HttpStatus.OK);
  }
}
