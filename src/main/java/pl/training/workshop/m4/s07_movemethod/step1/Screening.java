package pl.training.workshop.m4.s07_movemethod.step1;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Krok 1: Screening przejął opis seansu (Move Method z BookingPrinter.screeningLine).
 *
 * @param format    legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX
 * @param freeSeats numery wolnych miejsc (kolejność ze starego systemu, nie zawsze rosnąca)
 */
public record Screening(String title, int format, LocalDateTime start, int hall,
                        List<Integer> freeSeats) {
    public String headline() {
        String formatName = switch (format) {
            case 3 -> "IMAX";
            case 2 -> "3D";
            default -> "2D";
        };
        return title + " (" + formatName + "), sala " + hall + ", "
                + start.toLocalDate() + " " + start.toLocalTime();
    }
}
