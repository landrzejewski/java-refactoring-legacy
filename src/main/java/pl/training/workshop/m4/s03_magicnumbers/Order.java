package pl.training.workshop.m4.s03_magicnumbers;

import java.time.LocalTime;
import java.util.List;

/**
 * Stabilny kontrakt sceny.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param online zamówienie przez internet (opłata rezerwacyjna za bilet)
 */
public record Order(int format, LocalTime start, boolean online, List<Ticket> tickets) {
}
