package pl.training.workshop.m6.s02_polymorphism;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s02_polymorphism.step3.MarathonScreening;
import pl.training.workshop.m6.s02_polymorphism.step3.PremiereScreening;
import pl.training.workshop.m6.s02_polymorphism.step3.RegularScreening;
import pl.training.workshop.m6.s02_polymorphism.step3.Screening;

/** Po refaktoryzacji każdy rodzaj ma własne dane, a switch klienta jest sprawdzany przez kompilator. */
final class S02SolutionTest {
    @Test
    void mappingCreatesTheRightSubtypeWithNamedData() {
        Screening marathon = Screening.fromRow(new ScreeningRow("MARATHON", "Diuna", 2));
        assertEquals(new MarathonScreening("Diuna", 2), marathon);
    }

    @Test
    void clientSwitchIsExhaustiveWithoutDefault() {
        // dodanie czwartego rodzaju do permits zepsuje kompilację tej metody - to zaleta i koszt
        assertEquals("zwykly", badge(new RegularScreening("Amator", 120)));
        assertEquals("premiera", badge(new PremiereScreening("Diuna", 166)));
        assertEquals("maraton", badge(new MarathonScreening("Diuna", 2)));
    }

    private static String badge(Screening screening) {
        return switch (screening) {
            case RegularScreening regular -> "zwykly";
            case PremiereScreening premiere -> "premiera";
            case MarathonScreening marathon -> "maraton";
        };
    }
}
