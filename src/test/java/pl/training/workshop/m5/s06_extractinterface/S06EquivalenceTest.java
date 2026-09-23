package pl.training.workshop.m5.s06_extractinterface;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: podsumowanie koszyka (suma i VAT 8%/23%) identyczne w start i każdym kroku. */
final class S06EquivalenceTest {
    record Basket(List<String> tickets, List<String> snacks) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepSummarizesTheCartTheSameWay() {
        return Scene.<Basket, String>variants()
                .variant("start", b -> {
                    var cart = new pl.training.workshop.m5.s06_extractinterface.start.Cart();
                    b.tickets().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.start.Ticket("Diuna", "H7", Money.of(p))));
                    b.snacks().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.start.Snack("Popcorn", Money.of(p))));
                    return cart.summary();
                })
                .variant("step1", b -> {
                    var cart = new pl.training.workshop.m5.s06_extractinterface.step1.Cart();
                    b.tickets().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step1.Ticket("Diuna", "H7", Money.of(p))));
                    b.snacks().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step1.Snack("Popcorn", Money.of(p))));
                    return cart.summary();
                })
                .variant("step2", b -> {
                    var cart = new pl.training.workshop.m5.s06_extractinterface.step2.Cart();
                    b.tickets().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step2.Ticket("Diuna", "H7", Money.of(p))));
                    b.snacks().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step2.Snack("Popcorn", Money.of(p))));
                    return cart.summary();
                })
                .variant("step3", b -> {
                    var cart = new pl.training.workshop.m5.s06_extractinterface.step3.Cart();
                    b.tickets().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step3.Ticket("Diuna", "H7", Money.of(p))));
                    b.snacks().forEach(p -> cart.add(new pl.training.workshop.m5.s06_extractinterface.step3.Snack("Popcorn", Money.of(p))));
                    return cart.summary();
                })
                .expect("dwa bilety 2D i popcorn", new Basket(List.of("25.00", "25.00"), List.of("18.00")),
                        "Razem: 68.00, VAT: 7.07")
                .expect("bilet IMAX i napój", new Basket(List.of("40.00"), List.of("9.00")),
                        "Razem: 49.00, VAT: 4.64")
                .expect("sam bar", new Basket(List.of(), List.of("9.00")), "Razem: 9.00, VAT: 1.68")
                .expect("pusty koszyk", new Basket(List.of(), List.of()), "Razem: 0.00, VAT: 0.00")
                .tests();
    }
}
