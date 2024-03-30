package com.railwayreservationsystem.user.service.impl;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.model.Reservation;
import com.railwayreservationsystem.reservation.service.ReservationService;
import com.railwayreservationsystem.user.model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock
  @Qualifier("com.railwayreservationsystem.reservation.service.impl.ReservationServiceImpl")
  private ReservationService reservationService;

  @InjectMocks
  private UserServiceImpl userService;

  private InOrder inOrder;


  private UserModel setUpUserData() {
    return UserModel.builder().firstName("FirstName").lastName("LastName").email("test@example.com")
        .build();
  }


  public List<Reservation> setUpReservation() {
    Reservation reservation1 = new Reservation(1L, null, 12, setUpUserData(), "A");
    List<Reservation> reservations = new ArrayList<>();
    reservations.add(reservation1);
    return reservations;

  }

  @Before
  public void setUp() {
    inOrder = Mockito.inOrder(reservationService);
  }

  @After
  public void tearDown() {
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testRemoveUserReservation_Success() {

    when(reservationService.removeUserFromReservation("test@example.com")).thenReturn(true);
    boolean result = userService.removeUserReservation("test@example.com");

    inOrder.verify(reservationService).removeUserFromReservation("test@example.com");
    assertTrue(result);
  }

  @Test
  public void testModifyUserSeat_Success() {
    // Arrange
    when(reservationService.modifyUserSeat("test@example.com", "B", 11)).thenReturn(true);
    boolean result = userService.modifyUserSeat("test@example.com", "B", 11);

    inOrder.verify(reservationService).modifyUserSeat("test@example.com", "B", 11);
    assertTrue(result);
  }

  @Test
  public void testGetUserReservationDetails_Success() {

    Receipt receipt = buildReceiptData();
    when(reservationService.getUserReservationReceipt("test@example.com")).thenReturn(receipt);
    Receipt result = userService.getUserReservationDetails("test@example.com");

    inOrder.verify(reservationService).getUserReservationReceipt("test@example.com");

    // Assert
    assertNotNull(result);
    assertSame(receipt, result);
  }

  @Test
  public void testRemoveUserReservation_UserNotFound() {
    // Arrange
    when(reservationService.removeUserFromReservation("nonexistent@example.com")).thenReturn(false);

    boolean result = userService.removeUserReservation("nonexistent@example.com");
    inOrder.verify(reservationService).removeUserFromReservation("nonexistent@example.com");
    assertFalse(result);
  }

  @Test
  public void testModifyUserSeat_SeatAlreadyTaken() {
    // Arrange
    when(reservationService.modifyUserSeat("test@example.com", "A", 12)).thenReturn(false);

    boolean result = userService.modifyUserSeat("test@example.com", "A",
        12); // Assuming seat 12 is already taken in section A

    inOrder.verify(reservationService).modifyUserSeat("test@example.com", "A", 12);
    // Assert
    assertFalse(result);
  }

  private Receipt buildReceiptData() {
    return Receipt.builder().startPoint("London").destination("France").pricePaid(20.0)
        .user(setUpUserData()).build();
  }
}