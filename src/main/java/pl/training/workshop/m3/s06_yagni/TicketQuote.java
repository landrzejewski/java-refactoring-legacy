package pl.training.workshop.m3.s06_yagni;

import java.time.LocalTime;

/**
 * Stabilny kontrakt sceny - dane do wyceny miejsca.
 *
 * @param format     2D, 3D albo IMAX
 * @param start      godzina seansu (przed 12:00 - seans poranny, -5.00)
 * @param row        rząd miejsca
 * @param vipFromRow od tego rzędu miejsce jest VIP (+10.00)
 */
public record TicketQuote(String format, LocalTime start, int row, int vipFromRow) {
}
