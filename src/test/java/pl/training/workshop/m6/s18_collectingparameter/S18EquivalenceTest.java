package pl.training.workshop.m6.s18_collectingparameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Te same ostrzeżenia, w tej samej kolejności i z tym samym separatorem. */
final class S18EquivalenceTest {
    private static final LocalDateTime SHOW = LocalDateTime.of(2026, 10, 2, 18, 0);
    private static final LocalDateTime BEFORE = SHOW.minusHours(2);

    @TestFactory
    Stream<DynamicTest> everyStepCollectsTheSameWarnings() {
        return Scene.<ReservationDraft, String>variants()
                .variant("start", new pl.training.workshop.m6.s18_collectingparameter.start.ReservationValidator()::validate)
                .variant("step1", new pl.training.workshop.m6.s18_collectingparameter.step1.ReservationValidator()::validate)
                .variant("step2", new pl.training.workshop.m6.s18_collectingparameter.step2.ReservationValidator()::validate)
                .variant("step3", new pl.training.workshop.m6.s18_collectingparameter.step3.ReservationValidator()::validate)
                .expect("poprawna", new ReservationDraft("anna@kino.pl", List.of("A1", "A2"), SHOW, BEFORE), "OK")
                .expect("brak e-maila i miejsc", new ReservationDraft(" ", List.of(), SHOW, BEFORE),
                        "brak e-maila; brak miejsc")
                .expect("null e-mail", new ReservationDraft(null, List.of("A1"), SHOW, BEFORE), "brak e-maila")
                .expect("zły e-mail, duplikaty zgłoszone raz, po starcie",
                        new ReservationDraft("jan.kino.pl", List.of("A1", "A2", "A1", "A1", "A2"), SHOW, SHOW),
                        "niepoprawny e-mail: jan.kino.pl; miejsce A1 zdublowane; miejsce A2 zdublowane; "
                                + "seans juz sie rozpoczal")
                .expect("grupa 10+", new ReservationDraft("jan@kino.pl",
                        List.of("B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10"), SHOW, BEFORE),
                        "grupa 10+: zastosuj rabat grupowy")
                .tests();
    }
}
