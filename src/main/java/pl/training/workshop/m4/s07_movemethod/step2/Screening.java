package pl.training.workshop.m4.s07_movemethod.step2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Krok 2: Screening przejął też listę wolnych miejsc
 * (Move Method z BookingPrinter.remainingSeats).
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

    /**
     * Wolne miejsca bez wskazanego. Parametr MUSI zostać {@code Integer}: z {@code int}
     * wywołanie {@code remove} wybrałoby {@code remove(int index)} zamiast {@code remove(Object)}.
     */
    public List<Integer> freeSeatsWithout(Integer seat) {
        List<Integer> free = new ArrayList<>(freeSeats);
        free.remove(seat);
        return free;
    }
}
