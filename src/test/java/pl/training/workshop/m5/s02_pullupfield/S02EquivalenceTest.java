package pl.training.workshop.m5.s02_pullupfield;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: opis biletu (z normalizacją VIP) jest identyczny w start i każdym kroku. */
final class S02EquivalenceTest {
    record Sale(String kind, String seat, String studentId) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepDescribesSeatsTheSameWay() {
        return Scene.<Sale, String>variants()
                .variant("start", s -> new pl.training.workshop.m5.s02_pullupfield.start.BoxOffice().describe(s.kind(), s.seat(), s.studentId()))
                .variant("step1", s -> new pl.training.workshop.m5.s02_pullupfield.step1.BoxOffice().describe(s.kind(), s.seat(), s.studentId()))
                .variant("step2", s -> new pl.training.workshop.m5.s02_pullupfield.step2.BoxOffice().describe(s.kind(), s.seat(), s.studentId()))
                .variant("step3", s -> new pl.training.workshop.m5.s02_pullupfield.step3.BoxOffice().describe(s.kind(), s.seat(), s.studentId()))
                .expect("normalny - miejsce bez normalizacji", new Sale("NORMAL", "h7", null), "NORMAL h7")
                .expect("studencki", new Sale("STUDENT", "F3", "S-123"), "STUDENT F3 (legitymacja S-123)")
                .expect("VIP - wielkie litery", new Sale("VIP", "k12", null), "VIP K12")
                .tests();
    }
}
