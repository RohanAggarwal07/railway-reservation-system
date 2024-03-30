package com.railwayreservationsystem.user.service;

import com.railwayreservationsystem.reservation.model.Receipt;

public interface UserService {
  Boolean removeUserReservation(String email);

  Boolean modifyUserSeat(String email, String newSection, int newSeatNumber);

  Receipt getUserReservationDetails(String email);
}
