package com.railwayreservationsystem.user.service.impl;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.service.ReservationService;
import com.railwayreservationsystem.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service("com.railwayreservationsystem.user.service.impl.UserServiceImpl")
public class UserServiceImpl implements UserService {

  @Autowired
  @Qualifier("com.railwayreservationsystem.reservation.service.impl.ReservationServiceImpl")
  private ReservationService reservationService;

  @Override
  public Boolean removeUserReservation(String email) {
    log.info("Got request to remove reservation for user with email: {}", email);
    return reservationService.removeUserFromReservation(email);
  }

  @Override
  public Boolean modifyUserSeat(String email, String newSection, int newSeatNumber) {
    log.info("Request to modify user seat with email: {}, newSection: {}, newSeatNumber: {}", email,
        newSection, newSeatNumber);
    return reservationService.modifyUserSeat(email, newSection, newSeatNumber);
  }

  @Override
  public Receipt getUserReservationDetails(String email) {
    log.info("Request to get user reservation details by email: {} ", email);
    return reservationService.getUserReservationReceipt(email);
  }
}
