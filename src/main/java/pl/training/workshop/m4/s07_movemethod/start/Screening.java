package pl.training.workshop.m4.s07_movemethod.start;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seans - na razie sam worek danych; zachowanie o seansie mieszka w BookingPrinter.
 *
 * @param format    legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param freeSeats numery wolnych miejsc (kolejność ze starego systemu, nie zawsze rosnąca)
 */
public record Screening(String title, int format, LocalDateTime start, int hall,
                        List<Integer> freeSeats) {
}
