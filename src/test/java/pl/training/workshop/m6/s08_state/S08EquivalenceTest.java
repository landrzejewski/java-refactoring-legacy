package pl.training.workshop.m6.s08_state;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Tabela przejść zapisana PRZED refaktoryzacją: stan x akcja -&gt; wynik. Każdy wariant
 * musi dać ten sam status, te same efekty (w tej samej kolejności) i ten sam wyjątek.
 */
final class S08EquivalenceTest {
    /** Przypadek: jak dojść do stanu (akcje), jaka akcja jest testowana, czy bramka działa. */
    record Case(List<String> setup, String action, boolean gatewayDown) {
    }

    private static final String TABLE = """
            NEW       | pay    | PAID [charged]
            NEW       | use    | ERROR cannot use in NEW -> NEW []
            NEW       | expire | EXPIRED [seats released]
            NEW       | cancel | CANCELLED [seats released]
            PAID      | pay    | ERROR cannot pay in PAID -> PAID [charged]
            PAID      | use    | USED [charged, gate opened]
            PAID      | expire | ERROR cannot expire in PAID -> PAID [charged]
            PAID      | cancel | CANCELLED [charged, refund, seats released]
            USED      | pay    | ERROR cannot pay in USED -> USED [charged, gate opened]
            USED      | use    | ERROR cannot use in USED -> USED [charged, gate opened]
            USED      | expire | ERROR cannot expire in USED -> USED [charged, gate opened]
            USED      | cancel | ERROR cannot cancel in USED -> USED [charged, gate opened]
            EXPIRED   | pay    | ERROR cannot pay in EXPIRED -> EXPIRED [seats released]
            EXPIRED   | use    | ERROR cannot use in EXPIRED -> EXPIRED [seats released]
            EXPIRED   | expire | ERROR cannot expire in EXPIRED -> EXPIRED [seats released]
            EXPIRED   | cancel | ERROR cannot cancel in EXPIRED -> EXPIRED [seats released]
            CANCELLED | pay    | ERROR cannot pay in CANCELLED -> CANCELLED [seats released]
            CANCELLED | use    | ERROR cannot use in CANCELLED -> CANCELLED [seats released]
            CANCELLED | expire | ERROR cannot expire in CANCELLED -> CANCELLED [seats released]
            CANCELLED | cancel | ERROR cannot cancel in CANCELLED -> CANCELLED [seats released]
            """;

    @TestFactory
    Stream<DynamicTest> everyStepFollowsTheTransitionTable() {
        Scene<Case, String> scene = Scene.<Case, String>variants()
                .variant("start", c -> play(c, pl.training.workshop.m6.s08_state.start.Reservation::new))
                .variant("step1", c -> play(c, pl.training.workshop.m6.s08_state.step1.Reservation::new))
                .variant("step2", c -> play(c, pl.training.workshop.m6.s08_state.step2.Reservation::new))
                .variant("step3", c -> play(c, pl.training.workshop.m6.s08_state.step3.Reservation::new));
        for (String line : TABLE.strip().split("\n")) {
            String[] cells = line.split("\\|");
            String from = cells[0].strip();
            String action = cells[1].strip();
            scene.expect(from + " + " + action, new Case(setupFor(from), action, false), cells[2].strip());
        }
        return scene
                .expect("bramka niedostępna: stan i efekty bez zmian",
                        new Case(List.of(), "pay", true), "ERROR bramka niedostepna -> NEW []")
                .tests();
    }

    private static List<String> setupFor(String status) {
        return switch (status) {
            case "NEW" -> List.of();
            case "PAID" -> List.of("pay");
            case "USED" -> List.of("pay", "use");
            case "EXPIRED" -> List.of("expire");
            case "CANCELLED" -> List.of("cancel");
            default -> throw new IllegalArgumentException(status);
        };
    }

    private static String play(Case c, BiFunction<String, Payments, ReservationActions> factory) {
        ReservationActions reservation = factory.apply("R1", id -> {
            if (c.gatewayDown()) {
                throw new IllegalStateException("bramka niedostepna");
            }
        });
        c.setup().forEach(action -> apply(reservation, action));
        try {
            apply(reservation, c.action());
            return reservation.status() + " " + reservation.effects();
        } catch (RuntimeException exception) {
            return "ERROR " + exception.getMessage() + " -> " + reservation.status() + " " + reservation.effects();
        }
    }

    private static void apply(ReservationActions reservation, String action) {
        switch (action) {
            case "pay" -> reservation.pay();
            case "use" -> reservation.use();
            case "expire" -> reservation.expire();
            case "cancel" -> reservation.cancel();
            default -> throw new IllegalArgumentException(action);
        }
    }
}
