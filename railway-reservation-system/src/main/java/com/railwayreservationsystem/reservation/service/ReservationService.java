package com.railwayreservationsystem.reservation.service;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.model.Reservation;
import com.railwayreservationsystem.reservation.model.ReservationRequest;

import java.util.List;

public interface ReservationService {
  Boolean removeUserFromReservation(String userEmail);

  Boolean modifyUserSeat(String email, String newSection, int newSeatNumber);

  Receipt makeReservation(ReservationRequest request);

  List<Reservation> getReservationsBySection(String section);

  Receipt getUserReservationReceipt(String email);
}
