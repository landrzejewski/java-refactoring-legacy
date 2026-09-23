package pl.training.workshop.m3.s05_kiss.step2;

import java.util.List;

import pl.training.workshop.m3.s05_kiss.Hall;

/**
 * Krok 2 (rozwiązanie): Substitute Algorithm - zwykłe pętle zamiast regex i strumieni
 * indeksów. Złożoność istotna (co to znaczy "wolne miejsce", od którego rzędu VIP)
 * zostaje, ale jest nazwana: {@link #FREE} i {@link #vipRows}.
 */
public final class SeatCounter {
    private static final char FREE = '.';

    public String summary(Hall hall) {
        return "wolne: " + freeIn(hall.rows()) + ", wolne VIP: " + freeIn(vipRows(hall));
    }

    private List<String> vipRows(Hall hall) {
        int firstVipIndex = Math.clamp(hall.vipFromRow() - 1, 0, hall.rows().size());
        return hall.rows().subList(firstVipIndex, hall.rows().size());
    }

    private int freeIn(List<String> rows) {
        int free = 0;
        for (String row : rows) {
            for (char seat : row.toCharArray()) {
                if (seat == FREE) {
                    free++;
                }
            }
        }
        return free;
    }
}
