package pl.training.workshop.m4.s00_characterization;

import java.time.LocalTime;
import java.util.List;

/**
 * Stabilny kontrakt sceny - wspólny dla start i wszystkich kroków.
 *
 * @param format      legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param ticketTypes legacy kody biletów: "N" normalny, "S" student, "E" senior, "C" dziecko
 * @param online      rezerwacja przez internet (opłata rezerwacyjna) albo w kasie
 */
public record Booking(String customer, String title, int format, LocalTime start,
                      List<String> ticketTypes, boolean online) {
}
