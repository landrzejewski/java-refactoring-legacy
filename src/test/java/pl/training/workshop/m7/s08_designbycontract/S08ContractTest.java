package pl.training.workshop.m7.s08_designbycontract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Dla poprawnych wejść start i kroki są równoważne. Dla niepoprawnych - świadomie NIE:
 * dodanie kontroli kontraktu to zmiana zachowania i test pokazuje ją jawnie.
 */
final class S08ContractTest {
    @TestFactory
    Stream<DynamicTest> validUsageBehavesTheSame() {
        return Scene.<List<String>, String>variants()
                .variant("start", S08ContractTest::start)
                .variant("step1", S08ContractTest::step1)
                .variant("step2", S08ContractTest::step2)
                .expect("rezerwacja i zwolnienie", List.of("reserve 3", "reserve 90", "release 2"),
                        "reserve 3 -> true, zostalo 97; reserve 90 -> true, zostalo 7; release 2 -> zostalo 9")
                .expect("za malo miejsc to false, nie wyjatek", List.of("reserve 98", "reserve 5"),
                        "reserve 98 -> true, zostalo 2; reserve 5 -> false, zostalo 2")
                .expect("cala sala i zwrot wszystkiego", List.of("reserve 100", "release 100"),
                        "reserve 100 -> true, zostalo 0; release 100 -> zostalo 100")
                .tests();
    }

    @Test
    void startSilentlyCorruptsStateForInvalidInput() {
        assertEquals("reserve -2 -> true, zostalo 102", start(List.of("reserve -2")));
        assertEquals("reserve 10 -> true, zostalo 90; release 15 -> zostalo 105",
                start(List.of("reserve 10", "release 15")));
    }

    @Test
    void preconditionsRejectInvalidInputAndLeaveStateUntouched() {
        String negative = "reserve -2 -> IllegalArgumentException: seats must be positive, zostalo 100";
        assertEquals(negative, step1(List.of("reserve -2")));
        assertEquals(negative, step2(List.of("reserve -2")));
        String tooMany = "reserve 10 -> true, zostalo 90; "
                + "release 15 -> IllegalArgumentException: cannot release more seats than reserved, zostalo 90";
        assertEquals(tooMany, step1(List.of("reserve 10", "release 15")));
        assertEquals(tooMany, step2(List.of("reserve 10", "release 15")));
    }

    private static String start(List<String> ops) {
        var pool = new pl.training.workshop.m7.s08_designbycontract.start.SeatPool(100);
        return run(ops, pool::reserve, pool::release, pool::remaining);
    }

    private static String step1(List<String> ops) {
        var pool = new pl.training.workshop.m7.s08_designbycontract.step1.SeatPool(100);
        return run(ops, pool::reserve, pool::release, pool::remaining);
    }

    private static String step2(List<String> ops) {
        var pool = new pl.training.workshop.m7.s08_designbycontract.step2.SeatPool(100);
        return run(ops, pool::reserve, pool::release, pool::remaining);
    }

    /** Wykonuje operacje i zapisuje wektor: wynik albo wyjątek oraz stan po każdej operacji. */
    private static String run(List<String> ops, IntPredicate reserve, IntConsumer release, IntSupplier remaining) {
        return String.join("; ", ops.stream().map(op -> {
            String[] parts = op.split(" ");
            int seats = Integer.parseInt(parts[1]);
            Function<String, String> withState = outcome -> op + " -> " + outcome + "zostalo " + remaining.getAsInt();
            try {
                if (parts[0].equals("reserve")) {
                    return withState.apply(reserve.test(seats) + ", ");
                }
                release.accept(seats);
                return withState.apply("");
            } catch (IllegalArgumentException | IllegalStateException e) {
                return withState.apply(e.getClass().getSimpleName() + ": " + e.getMessage() + ", ");
            }
        }).toList());
    }
}
