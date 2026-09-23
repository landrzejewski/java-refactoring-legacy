package pl.training.workshop.m6.s04_encapsulatefactory;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Sprzedaż pojedyncza i grupowa daje te same bilety w każdym kroku. */
final class S04EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepSellsTheSameTickets() {
        return Scene.<SeatSale, String>variants()
                .variant("start", s -> {
                    var office = new pl.training.workshop.m6.s04_encapsulatefactory.start.BoxOffice();
                    return office.sell(s.title(), s.base(), s.rows().getFirst()).describe() + " | "
                            + String.join("; ", office.sellAll(s.title(), s.base(), s.rows()).stream()
                            .map(t -> t.describe()).toList());
                })
                .variant("step1", s -> {
                    var office = new pl.training.workshop.m6.s04_encapsulatefactory.step1.BoxOffice();
                    return office.sell(s.title(), s.base(), s.rows().getFirst()).describe() + " | "
                            + String.join("; ", office.sellAll(s.title(), s.base(), s.rows()).stream()
                            .map(t -> t.describe()).toList());
                })
                .variant("step2", s -> {
                    var office = new pl.training.workshop.m6.s04_encapsulatefactory.step2.BoxOffice();
                    return office.sell(s.title(), s.base(), s.rows().getFirst()).describe() + " | "
                            + String.join("; ", office.sellAll(s.title(), s.base(), s.rows()).stream()
                            .map(t -> t.describe()).toList());
                })
                .variant("step3", s -> {
                    var office = new pl.training.workshop.m6.s04_encapsulatefactory.step3.BoxOffice();
                    return office.sell(s.title(), s.base(), s.rows().getFirst()).describe() + " | "
                            + String.join("; ", office.sellAll(s.title(), s.base(), s.rows()).stream()
                            .map(t -> t.describe()).toList());
                })
                .expect("IMAX, granica VIP na rzędzie 10",
                        new SeatSale("Diuna", Money.of("40.00"), List.of(9, 10, 12)),
                        "Diuna r9 40.00 | Diuna r9 40.00; Diuna r10 VIP 50.00; Diuna r12 VIP 50.00")
                .expect("2D, pierwsze miejsce VIP",
                        new SeatSale("Amator", Money.of("25.00"), List.of(11, 1)),
                        "Amator r11 VIP 35.00 | Amator r11 VIP 35.00; Amator r1 25.00")
                .tests();
    }
}
