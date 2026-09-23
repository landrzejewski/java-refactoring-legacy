package pl.training.workshop.m6.s05_extractfactory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Scenariusz kilku rezerwacji na jednym serwisie. Linia skryptu: "KANAŁ email miejsca",
 * kanał GROUP oznacza reserveGroup.
 */
final class S05EquivalenceTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-02T16:00:00Z"), ZoneOffset.UTC);

    interface Reserve {
        Reservation reserve(String channel, String email, List<String> seats);
    }

    @TestFactory
    Stream<DynamicTest> everyStepCreatesTheSameReservations() {
        return Scene.<List<String>, String>variants()
                .variant("start", script -> {
                    var service = new pl.training.workshop.m6.s05_extractfactory.start.ReservationService(CLOCK);
                    return play(script, service::reserve, service::reserveGroup);
                })
                .variant("step1", script -> {
                    var service = new pl.training.workshop.m6.s05_extractfactory.step1.ReservationService(CLOCK);
                    return play(script, service::reserve, service::reserveGroup);
                })
                .variant("step2", script -> {
                    var service = new pl.training.workshop.m6.s05_extractfactory.step2.ReservationService(CLOCK);
                    return play(script, service::reserve, service::reserveGroup);
                })
                .variant("step3", script -> {
                    var service = new pl.training.workshop.m6.s05_extractfactory.step3.ReservationService(CLOCK);
                    return play(script, service::reserve, service::reserveGroup);
                })
                .expect("kasa i online", List.of(
                                "BOX_OFFICE jan@kino.pl A1,A2",
                                "ONLINE anna@kino.pl A3"),
                        """
                                R1 BOX_OFFICE [A1, A2] fee=0.00 expires=null
                                R2 ONLINE [A3] fee=2.00 expires=2026-10-02T16:15
                                """)
                .expect("zajęte miejsce nie zużywa numeru, nieznany kanał zużywa", List.of(
                                "ONLINE anna@kino.pl A1",
                                "ONLINE jan@kino.pl A1",
                                "PHONE jan@kino.pl B1",
                                "BOX_OFFICE jan@kino.pl B1"),
                        """
                                R1 ONLINE [A1] fee=2.00 expires=2026-10-02T16:15
                                IllegalStateException: seat taken: A1
                                IllegalArgumentException: unknown channel: PHONE
                                R3 BOX_OFFICE [B1] fee=0.00 expires=null
                                """)
                .expect("grupa", List.of(
                                "GROUP jan@kino.pl C1,C2",
                                "GROUP jan@kino.pl C1,C2,C3,C4,C5,C6,C7,C8,C9,C10"),
                        """
                                IllegalArgumentException: group needs 10+ seats
                                R1 ONLINE [C1, C2, C3, C4, C5, C6, C7, C8, C9, C10] fee=20.00 \
                                expires=2026-10-02T16:15
                                """)
                .tests();
    }

    private static String play(List<String> script, Reserve reserve,
                               BiFunction<String, List<String>, Reservation> group) {
        List<String> lines = new ArrayList<>();
        for (String line : script) {
            String[] parts = line.split(" ");
            List<String> seats = Arrays.asList(parts[2].split(","));
            try {
                Reservation r = parts[0].equals("GROUP")
                        ? group.apply(parts[1], seats)
                        : reserve.reserve(parts[0], parts[1], seats);
                lines.add(r.id() + " " + r.channel() + " " + r.seats() + " fee=" + r.fee()
                        + " expires=" + r.expiresAt());
            } catch (RuntimeException exception) {
                lines.add(exception.getClass().getSimpleName() + ": " + exception.getMessage());
            }
        }
        return String.join("\n", lines) + "\n";
    }
}
