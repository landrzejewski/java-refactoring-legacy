package pl.training.workshop.m4.s07_movemethod;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stabilne wejście testu - z niego każdy wariant buduje własne Screening i Booking
 * (typy te zmieniają się w trakcie sceny, więc mieszkają w pakietach start/stepN).
 */
public record BookingData(String id, String title, int format, LocalDateTime start, int hall,
                          List<Integer> freeSeats, Integer seat) {
}
