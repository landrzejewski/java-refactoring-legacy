package pl.training.workshop.m3.s05_kiss.step1;

import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pl.training.workshop.m3.s05_kiss.Hall;

/**
 * Krok 1: Replace Parameter with Explicit Methods - zamiast stringa "all"/"vip"
 * i refleksji dwie jawne metody. Kompilator i IDE znów widzą przepływ,
 * literówka nie przejdzie kompilacji.
 */
public final class SeatCounter {
    private static final Pattern FREE = Pattern.compile("(?<seat>\\.)");

    public String summary(Hall hall) {
        return "wolne: " + freeSeats(hall) + ", wolne VIP: " + freeVipSeats(hall);
    }

    private long freeSeats(Hall hall) {
        return countFree(hall.rows().stream());
    }

    private long freeVipSeats(Hall hall) {
        return countFree(IntStream.rangeClosed(1, hall.rows().size())
                .filter(n -> n >= hall.vipFromRow())
                .mapToObj(n -> hall.rows().get(n - 1)));
    }

    private long countFree(Stream<String> rows) {
        return rows.flatMap(row -> FREE.matcher(row).results().map(m -> m.group("seat"))).count();
    }
}
