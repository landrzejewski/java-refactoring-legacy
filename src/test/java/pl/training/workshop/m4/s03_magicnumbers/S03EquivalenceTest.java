package pl.training.workshop.m4.s03_magicnumbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: nazwanie liczb nie zmienia ani grosza w podsumowaniu. */
final class S03EquivalenceTest {
    /** Pułapka "stałej" kolekcji: final chroni referencję, nie zawartość */
    private final List<String> discountedTypes = new ArrayList<>(List.of("S", "E", "C"));
    private final List<String> discountedTypesImmutable = List.of("S", "E", "C");

    @TestFactory
    Stream<DynamicTest> everyStepSummarizesOrdersTheSameWay() {
        return Scene.<Order, String>variants()
                .variant("start", new pl.training.workshop.m4.s03_magicnumbers.start.OrderPricer()::summary)
                .variant("step1", new pl.training.workshop.m4.s03_magicnumbers.step1.OrderPricer()::summary)
                .variant("step2", new pl.training.workshop.m4.s03_magicnumbers.step2.OrderPricer()::summary)
                .variant("step3", new pl.training.workshop.m4.s03_magicnumbers.step3.OrderPricer()::summary)
                .expect("2D wieczorem online: normalny + student na VIP",
                        new Order(1, LocalTime.of(18, 0), true,
                                List.of(new Ticket("N", 5), new Ticket("S", 10))),
                        "Bilety: 53.75, oplata: 4.00, razem: 57.75, punkty: 5")
                .expect("3D rano w kasie: senior + dziecko na VIP",
                        new Order(2, LocalTime.of(10, 30), false,
                                List.of(new Ticket("E", 3), new Ticket("C", 11))),
                        "Bilety: 41.60, oplata: 0.00, razem: 41.60, punkty: 4")
                .expect("IMAX online, grupa 10 biletów",
                        new Order(3, LocalTime.of(20, 0), true, Collections.nCopies(10, new Ticket("N", 1))),
                        "Bilety: 360.00, oplata: 20.00, razem: 380.00, punkty: 36")
                .expect("2D 12:00 w kasie, 9 biletów VIP - jeszcze nie grupa",
                        new Order(1, LocalTime.of(12, 0), false, Collections.nCopies(9, new Ticket("N", 10))),
                        "Bilety: 315.00, oplata: 0.00, razem: 315.00, punkty: 31")
                .tests();
    }

    @Test
    void finalDoesNotMakeACollectionConstant() {
        discountedTypes.add("N");
        assertEquals(List.of("S", "E", "C", "N"), discountedTypes,
                "ktoś właśnie dał zniżkę biletom normalnym");
        assertThrows(UnsupportedOperationException.class, () -> discountedTypesImmutable.add("N"));
    }
}
