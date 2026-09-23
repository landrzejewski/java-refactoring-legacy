package pl.training.workshop.m3.s05_kiss.start;

import java.lang.reflect.Method;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pl.training.workshop.m3.s05_kiss.Hall;

/**
 * Start: złożoność wprowadzona. Rodzaj miejsc wybierany stringiem i refleksją
 * ("all" -> allRows, "vip" -> vipRows), wolne miejsca liczone wyrażeniem regularnym
 * z nazwaną grupą. Działa, ale IDE widzi metody wierszy jako nieużywane,
 * literówka w "vip" wybucha dopiero w runtime, a przepływ jest ukryty.
 */
public final class SeatCounter {
    private static final Pattern FREE = Pattern.compile("(?<seat>\\.)");

    public String summary(Hall hall) {
        return "wolne: " + free(hall, "all") + ", wolne VIP: " + free(hall, "vip");
    }

    long free(Hall hall, String kind) {
        try {
            Method rows = getClass().getDeclaredMethod(kind + "Rows", Hall.class);
            @SuppressWarnings("unchecked")
            Stream<String> selected = (Stream<String>) rows.invoke(this, hall);
            return selected.flatMap(row -> FREE.matcher(row).results().map(m -> m.group("seat"))).count();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("nieznany rodzaj miejsc: " + kind, e);
        }
    }

    @SuppressWarnings("unused") // wołane refleksją
    private Stream<String> allRows(Hall hall) {
        return hall.rows().stream();
    }

    @SuppressWarnings("unused") // wołane refleksją
    private Stream<String> vipRows(Hall hall) {
        return IntStream.rangeClosed(1, hall.rows().size())
                .filter(n -> n >= hall.vipFromRow())
                .mapToObj(n -> hall.rows().get(n - 1));
    }
}
