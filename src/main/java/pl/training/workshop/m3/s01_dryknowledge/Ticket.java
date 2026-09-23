package pl.training.workshop.m3.s01_dryknowledge;

import java.time.LocalTime;

/**
 * Stabilny kontrakt sceny - bilet do sprzedaży lub zwrotu.
 *
 * @param format 2D, 3D albo IMAX
 * @param type   NORMAL, STUDENT, SENIOR albo CHILD
 * @param start  godzina rozpoczęcia seansu (przed 12:00 - seans poranny)
 */
public record Ticket(String format, String type, LocalTime start) {
}
