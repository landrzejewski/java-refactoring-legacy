package pl.training.workshop.m7.s06_parameterobject;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności dla poprawnych terminów: nowe API (od kroku 1) i stare sygnatury. */
final class S06EquivalenceTest {
    /** Surowe dane wejściowe - te same dla wszystkich wariantów (Parameter Object powstaje dopiero w krokach). */
    record Clump(String screeningId, LocalDate date, int hall, String format) {
    }

    static final LocalDate DAY = LocalDate.of(2026, 3, 10);

    @TestFactory
    Stream<DynamicTest> newApiBehavesLikeTheOldOne() {
        return cases(Scene.<Clump, String>variants()
                .variant("start", S06EquivalenceTest::start)
                .variant("step1", S06EquivalenceTest::step1)
                .variant("step2", S06EquivalenceTest::step2)
                .variant("step3", S06EquivalenceTest::step3));
    }

    @TestFactory
    @SuppressWarnings("deprecation")
    Stream<DynamicTest> deprecatedSignaturesStillWork() {
        var step1 = new pl.training.workshop.m7.s06_parameterobject.step1.ScreeningPlanner();
        var step2 = new pl.training.workshop.m7.s06_parameterobject.step2.ScreeningPlanner();
        var step3 = new pl.training.workshop.m7.s06_parameterobject.step3.ScreeningPlanner();
        return cases(Scene.<Clump, String>variants()
                .variant("step1", c -> step1.describe(c.screeningId(), c.date(), c.hall(), c.format())
                        + " | " + step1.ticketPrice(c.screeningId(), c.date(), c.hall(), c.format()))
                .variant("step2", c -> step2.describe(c.screeningId(), c.date(), c.hall(), c.format())
                        + " | " + step2.ticketPrice(c.screeningId(), c.date(), c.hall(), c.format()))
                .variant("step3", c -> step3.describe(c.screeningId(), c.date(), c.hall(), c.format())
                        + " | " + step3.ticketPrice(c.screeningId(), c.date(), c.hall(), c.format())));
    }

    private static Stream<DynamicTest> cases(Scene<Clump, String> scene) {
        return scene
                .expect("IMAX w sali 1", new Clump("S1", DAY, 1, "IMAX"), "S1 2026-03-10 sala 1 (IMAX) | 40.00")
                .expect("3D w sali 2", new Clump("S2", DAY, 2, "3D"), "S2 2026-03-10 sala 2 (3D) | 32.00")
                .expect("2D w sali 8 (granica)", new Clump("S3", DAY, 8, "2D"), "S3 2026-03-10 sala 8 (2D) | 25.00")
                .tests();
    }

    static String start(Clump c) {
        var planner = new pl.training.workshop.m7.s06_parameterobject.start.ScreeningPlanner();
        return attempt(x -> planner.describe(c.screeningId(), c.date(), c.hall(), c.format()), c)
                + " | " + attempt(x -> planner.ticketPrice(c.screeningId(), c.date(), c.hall(), c.format()), c);
    }

    static String step1(Clump c) {
        var planner = new pl.training.workshop.m7.s06_parameterobject.step1.ScreeningPlanner();
        var slot = new pl.training.workshop.m7.s06_parameterobject.step1.ScreeningSlot(
                c.screeningId(), c.date(), c.hall(), c.format());
        return attempt(planner::describe, slot) + " | " + attempt(planner::ticketPrice, slot);
    }

    static String step2(Clump c) {
        var planner = new pl.training.workshop.m7.s06_parameterobject.step2.ScreeningPlanner();
        var slot = new pl.training.workshop.m7.s06_parameterobject.step2.ScreeningSlot(
                c.screeningId(), c.date(), c.hall(), c.format());
        return attempt(planner::describe, slot) + " | " + attempt(planner::ticketPrice, slot);
    }

    static String step3(Clump c) {
        var planner = new pl.training.workshop.m7.s06_parameterobject.step3.ScreeningPlanner();
        pl.training.workshop.m7.s06_parameterobject.step3.ScreeningSlot slot;
        try {
            slot = new pl.training.workshop.m7.s06_parameterobject.step3.ScreeningSlot(
                    c.screeningId(), c.date(), c.hall(), c.format());
        } catch (IllegalArgumentException e) {
            return "new ScreeningSlot -> EXC " + e.getMessage();
        }
        return attempt(planner::describe, slot) + " | " + attempt(planner::ticketPrice, slot);
    }

    /** Wynik albo komunikat wyjątku - wyjątek też jest częścią obserwowalnego zachowania. */
    static <T> String attempt(Function<T, ?> call, T input) {
        try {
            return String.valueOf(call.apply(input));
        } catch (IllegalArgumentException e) {
            return "EXC " + e.getMessage();
        }
    }
}
