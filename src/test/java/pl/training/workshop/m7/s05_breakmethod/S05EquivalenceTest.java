package pl.training.workshop.m7.s05_breakmethod;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: ten sam tekst, ten sam wyjątek (typ i komunikat)
 * i nietknięta lista wejściowa w start i w każdym kroku.
 */
final class S05EquivalenceTest {
    private static final Screening DIUNA = new Screening("Diuna", "IMAX", LocalTime.of(20, 0), 1, false);
    private static final Screening KRAINA = new Screening("Kraina Lodu", "3D", LocalTime.of(11, 0), 2, false);
    private static final Screening AMATOR = new Screening("Amator", "2D", LocalTime.of(18, 30), 3, false);
    private static final Screening AMATOR_LATE = new Screening("Amator", "2D", LocalTime.of(21, 0), 3, true);
    private static final Screening ALIEN = new Screening("Alien", "2D", LocalTime.of(20, 0), 4, false);

    @TestFactory
    Stream<DynamicTest> everyStepBuildsTheSameRepertoire() {
        return Scene.<List<Screening>, String>variants()
                .variant("start", observe(new pl.training.workshop.m7.s05_breakmethod.start.RepertoireBuilder()::build))
                .variant("step1", observe(new pl.training.workshop.m7.s05_breakmethod.step1.RepertoireBuilder()::build))
                .variant("step2", observe(new pl.training.workshop.m7.s05_breakmethod.step2.RepertoireBuilder()::build))
                .variant("step3", observe(new pl.training.workshop.m7.s05_breakmethod.step3.RepertoireBuilder()::build))
                .expect("sortowanie po godzinie, potem tytule; odwolany pominiety",
                        List.of(DIUNA, KRAINA, AMATOR_LATE, AMATOR, ALIEN),
                        """
                                REPERTUAR
                                11:00 Kraina Lodu (3D), sala 2
                                18:30 Amator (2D), sala 3
                                20:00 Alien (2D), sala 4
                                20:00 Diuna (IMAX), sala 1
                                | wejscie bez zmian: true""")
                .expect("same odwolane",
                        List.of(AMATOR_LATE),
                        """
                                REPERTUAR
                                brak seansow
                                | wejscie bez zmian: true""")
                .expect("null w srodku listy",
                        Arrays.asList(DIUNA, null, KRAINA),
                        "IllegalArgumentException: screening must not be null | wejscie bez zmian: true")
                .tests();
    }

    /** Wektor zachowania: wynik albo wyjątek, plus to, czy lista klienta nie została zmieniona. */
    private static Function<List<Screening>, String> observe(Function<List<Screening>, String> build) {
        return input -> {
            List<Screening> clientList = new ArrayList<>(input);
            List<Screening> before = new ArrayList<>(clientList);
            String result;
            try {
                result = build.apply(clientList);
            } catch (IllegalArgumentException e) {
                result = "IllegalArgumentException: " + e.getMessage() + " ";
            }
            return result + "| wejscie bez zmian: " + before.equals(clientList);
        };
    }
}
