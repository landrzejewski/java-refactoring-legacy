package pl.training.workshop.m4.s06_inlinemethod;

import java.time.LocalTime;

/**
 * Stabilny kontrakt sceny.
 *
 * @param format legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 */
public record Ticket(int format, LocalTime start) {
}
