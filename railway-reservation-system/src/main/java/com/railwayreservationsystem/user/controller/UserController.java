package com.railwayreservationsystem.user.controller;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  @Qualifier("com.railwayreservationsystem.user.service.impl.UserServiceImpl")
  private UserService userService;

  @GetMapping("/reservationDetails")
  public ResponseEntity<Receipt> getUserReservationDetails(@RequestParam String email) {
    Receipt reservationDetails = userService.getUserReservationDetails(email);
    if (Objects.nonNull(reservationDetails)) {
      return new ResponseEntity<>(reservationDetails, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/removeReservation")
  public ResponseEntity<String> removeUserFromReservation(@RequestParam String email) {
    Boolean userRemoved = userService.removeUserReservation(email);
    if (Boolean.TRUE.equals(userRemoved)) {
      return new ResponseEntity<>("User removed from reservation successfully", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Failed to remove user reservation",
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/modifySeat")
  public ResponseEntity<String> modifyUserSeat(@RequestParam String email,
      @RequestParam String newSection, @RequestParam int newSeatNumber) {
    Boolean modified = userService.modifyUserSeat(email, newSection, newSeatNumber);
    if (Boolean.TRUE.equals(modified)) {
      return new ResponseEntity<>("User's seat modified successfully.", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Failed to modify user seat", HttpStatus.OK);
    }
  }
}

