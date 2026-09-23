package pl.training.workshop.m3.s04_drytests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Dla poprawnej taryfy każda wersja specyfikacji jest zielona. Różnica wychodzi dopiero,
 * gdy taryfa ma błąd: krok 1 (i start, z tą samą wyrocznią) go nie widzi - pułapka;
 * krok 2 go łapie. Pułapkę sprawdzamy na kopii z kroku 1, bo start jest edytowany na żywo.
 */
final class S04SpecsTest {
    private static final Tariff BUGGY = Tariff.standard().withDiscount("STUDENT", 20);

    @TestFactory
    Stream<DynamicTest> everyVersionAcceptsCorrectTariff() {
        return Scene.<Tariff, List<String>>variants()
                .variant("start", t -> new pl.training.workshop.m3.s04_drytests.start.TicketPriceSpecs()
                        .run(new TicketPrice(t)))
                .variant("step1", t -> new pl.training.workshop.m3.s04_drytests.step1.TicketPriceSpecs()
                        .run(new TicketPrice(t)))
                .variant("step2", t -> new pl.training.workshop.m3.s04_drytests.step2.TicketPriceSpecs()
                        .run(new TicketPrice(t)))
                .expect("taryfa zgodna z regulaminem", Tariff.standard(), List.of())
                .tests();
    }

    @Test
    void step1IsReadableButStillBlindToTheBug() {
        assertEquals(List.of(), new pl.training.workshop.m3.s04_drytests.step1.TicketPriceSpecs()
                .run(new TicketPrice(BUGGY)));
    }

    @Test
    void step2CatchesBrokenStudentDiscount() {
        assertEquals(List.of("student na porannym 3D: oczekiwano 19.00, jest 20.60"),
                new pl.training.workshop.m3.s04_drytests.step2.TicketPriceSpecs().run(new TicketPrice(BUGGY)));
    }
}
