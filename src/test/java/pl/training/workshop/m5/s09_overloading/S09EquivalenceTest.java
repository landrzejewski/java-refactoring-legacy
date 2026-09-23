package pl.training.workshop.m5.s09_overloading;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Wspólna część wszystkich wariantów: wywołanie z typem DEKLAROWANYM StudentTicket.
 * Tak wyglądał klient przed Extract Superclass - i tu wszystkie wersje są zgodne.
 */
final class S09EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesStaticallyTypedStudentTicket() {
        return Scene.<String, String>variants()
                .variant("start", p -> new pl.training.workshop.m5.s09_overloading.start.PriceList()
                        .price(new pl.training.workshop.m5.s09_overloading.start.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step1", p -> new pl.training.workshop.m5.s09_overloading.step1.PriceList()
                        .price(new pl.training.workshop.m5.s09_overloading.step1.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step2", p -> new pl.training.workshop.m5.s09_overloading.step2.PriceList()
                        .price(new pl.training.workshop.m5.s09_overloading.step2.StudentTicket("Amator", Money.of(p))).toString())
                .expect("studencki 2D", "25.00", "18.75")
                .expect("studencki IMAX", "40.00", "30.00")
                .tests();
    }
}
