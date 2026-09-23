package pl.training.workshop.m4.s10_encapsulatefield;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * start, step1, step2 - refaktoryzacja: identyczny ślad statusów, także dla niedozwolonych przejść.
 * step3 - świadoma zmiana zachowania: dozwolone ścieżki bez zmian, niedozwolone kończą się BLAD.
 */
final class S10EquivalenceTest {
    private static final List<String> PAY_AND_ENTER = List.of("pay", "checkIn");
    private static final List<String> CANCEL = List.of("cancel");
    private static final List<String> GUEST = List.of("guest");
    private static final List<String> EXPIRED_THEN_PAY = List.of("expire", "pay");
    private static final List<String> PAY_TWICE = List.of("pay", "pay");
    private static final List<String> ENTER_UNPAID = List.of("checkIn");
    private static final List<String> GUEST_ON_CANCELLED = List.of("cancel", "guest");

    @TestFactory
    Stream<DynamicTest> encapsulationKeepsBehaviour() {
        return withAllowedPaths(Scene.<List<String>, String>variants()
                .variant("start", commands -> {
                    var office = new pl.training.workshop.m4.s10_encapsulatefield.start.BoxOffice();
                    var r = new pl.training.workshop.m4.s10_encapsulatefield.start.Reservation();
                    return trace(commands, command -> switch (command) {
                        case "pay" -> office::pay;
                        case "checkIn" -> office::checkIn;
                        case "cancel" -> office::cancel;
                        case "expire" -> office::expire;
                        default -> office::guestEntry;
                    }, r, () -> r.status);
                })
                .variant("step1", commands -> {
                    var office = new pl.training.workshop.m4.s10_encapsulatefield.step1.BoxOffice();
                    var r = new pl.training.workshop.m4.s10_encapsulatefield.step1.Reservation();
                    return trace(commands, command -> switch (command) {
                        case "pay" -> office::pay;
                        case "checkIn" -> office::checkIn;
                        case "cancel" -> office::cancel;
                        case "expire" -> office::expire;
                        default -> office::guestEntry;
                    }, r, r::getStatus);
                })
                .variant("step2", commands -> {
                    var office = new pl.training.workshop.m4.s10_encapsulatefield.step2.BoxOffice();
                    var r = new pl.training.workshop.m4.s10_encapsulatefield.step2.Reservation();
                    return trace(commands, command -> switch (command) {
                        case "pay" -> office::pay;
                        case "checkIn" -> office::checkIn;
                        case "cancel" -> office::cancel;
                        case "expire" -> office::expire;
                        default -> office::guestEntry;
                    }, r, r::status);
                }))
                .expect("ZASTANE: płatność po wygaśnięciu cicho ignorowana", EXPIRED_THEN_PAY, "EXPIRED,EXPIRED")
                .expect("ZASTANE: druga płatność cicho ignorowana", PAY_TWICE, "PAID,PAID")
                .expect("ZASTANE: wejście bez płatności cicho ignorowane", ENTER_UNPAID, "NEW")
                .expect("ZASTANE: gość wchodzi na anulowaną rezerwację", GUEST_ON_CANCELLED, "CANCELLED,USED")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> step3RejectsIllegalTransitions() {
        return withAllowedPaths(Scene.<List<String>, String>variants()
                .variant("step3", commands -> {
                    var office = new pl.training.workshop.m4.s10_encapsulatefield.step3.BoxOffice();
                    var r = new pl.training.workshop.m4.s10_encapsulatefield.step3.Reservation();
                    return trace(commands, command -> switch (command) {
                        case "pay" -> office::pay;
                        case "checkIn" -> office::checkIn;
                        case "cancel" -> office::cancel;
                        case "expire" -> office::expire;
                        default -> office::guestEntry;
                    }, r, r::status);
                }))
                .expect("ZMIANA: płatność po wygaśnięciu odrzucona", EXPIRED_THEN_PAY, "EXPIRED,BLAD")
                .expect("ZMIANA: druga płatność odrzucona", PAY_TWICE, "PAID,BLAD")
                .expect("ZMIANA: wejście bez płatności odrzucone", ENTER_UNPAID, "BLAD")
                .expect("ZMIANA: gość nie wejdzie na anulowaną rezerwację", GUEST_ON_CANCELLED, "CANCELLED,BLAD")
                .tests();
    }

    @Test
    void startFieldIsPublicFromStep1ItIsNot() throws ReflectiveOperationException {
        pl.training.workshop.m4.s10_encapsulatefield.start.Reservation.class.getField("status");
        assertThrows(NoSuchFieldException.class,
                () -> pl.training.workshop.m4.s10_encapsulatefield.step1.Reservation.class.getField("status"));
    }

    /** Ścieżki dozwolone - wspólne oczekiwania dla wszystkich wariantów, także step3. */
    private static Scene<List<String>, String> withAllowedPaths(Scene<List<String>, String> scene) {
        return scene
                .expect("zapłata i wejście", PAY_AND_ENTER, "PAID,USED")
                .expect("anulowanie nowej", CANCEL, "CANCELLED")
                .expect("gość na nową rezerwację", GUEST, "USED");
    }

    /** Wykonuje polecenia i zapisuje status po każdym (albo BLAD, gdy operacja rzuciła wyjątek). */
    private static <R> String trace(List<String> commands, Function<String, Consumer<R>> action,
                                    R reservation, Supplier<String> status) {
        List<String> trace = new ArrayList<>();
        for (String command : commands) {
            try {
                action.apply(command).accept(reservation);
                trace.add(status.get());
            } catch (IllegalStateException e) {
                trace.add("BLAD");
            }
        }
        return String.join(",", trace);
    }
}
