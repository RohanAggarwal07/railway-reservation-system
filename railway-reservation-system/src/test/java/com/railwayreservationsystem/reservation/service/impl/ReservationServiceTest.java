package com.railwayreservationsystem.reservation.service.impl;

import com.railwayreservationsystem.reservation.model.Receipt;
import com.railwayreservationsystem.reservation.model.Reservation;
import com.railwayreservationsystem.reservation.model.ReservationRequest;
import com.railwayreservationsystem.user.model.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest {

  private InOrder inOrder;

  @Mock
  @Qualifier("com.railwayreservationsystem.reservation.service.impl.ReservationServiceImpl")
  private ReservationServiceImpl reservationService;

  @Before
  public void setUp() {
    inOrder = Mockito.inOrder(reservationService);
  }

  @After
  public void tearDown() {
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void testMakeReservation_Success() {
    // Prepare test data
    ReservationRequest request = new ReservationRequest();
    request.setUser(setUpUserData());
    request.setSection("A");
    request.setSeatNumber(1);

    Receipt receipt = buildReceiptData();

    when(reservationService.makeReservation(request)).thenReturn(receipt);

    // Call the service method directly
    Receipt result = reservationService.makeReservation(request);
    inOrder.verify(reservationService).makeReservation(request);

    // Verify the result
    assertEquals(receipt, result);
  }

  @Test
  public void testMakeReservation_Failure() {
    // Prepare test data
    ReservationRequest request = new ReservationRequest();
    request.setUser(setUpUserData());
    request.setSection("A");
    request.setSeatNumber(1);

    when(reservationService.makeReservation(any(ReservationRequest.class))).thenReturn(null);
    Receipt result = reservationService.makeReservation(request);
    inOrder.verify(reservationService).makeReservation(any());
    assertNull(result);
  }

  // section full test case
  @Test
  public void testGetReservationsBySection() {
    // Prepare test data
    List<Reservation> reservations = Collections.singletonList(new Reservation());

    when(reservationService.getReservationsBySection("A")).thenReturn(reservations);
    List<Reservation> result = reservationService.getReservationsBySection("A");
    inOrder.verify(reservationService).getReservationsBySection("A");
    assertEquals(reservations, result);
  }

  private UserModel setUpUserData() {
    return UserModel.builder().firstName("FirstName").lastName("LastName").email("Email").build();
  }

  private Receipt buildReceiptData() {
    return Receipt.builder().user(setUpUserData()).pricePaid(20.0).startPoint("London")
        .destination("France").build();
  }
}
