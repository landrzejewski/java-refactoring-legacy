package pl.training.workshop.m5.s14_reuse;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: naliczanie punktów i raport identyczne dla obu kont w start i każdym kroku. */
final class S14EquivalenceTest {
    record History(boolean corporate, String owner, List<String> payments) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepReportsPointsTheSameWay() {
        return Scene.<History, String>variants()
                .variant("start", h -> {
                    if (h.corporate()) {
                        var account = new pl.training.workshop.m5.s14_reuse.start.CorporateAccount(h.owner());
                        h.payments().forEach(p -> account.earn(Money.of(p)));
                        return new pl.training.workshop.m5.s14_reuse.start.LoyaltyReport().line(account);
                    }
                    var account = new pl.training.workshop.m5.s14_reuse.start.LoyaltyAccount(h.owner());
                    h.payments().forEach(p -> account.earn(Money.of(p)));
                    return new pl.training.workshop.m5.s14_reuse.start.LoyaltyReport().line(account);
                })
                .variant("step1", h -> {
                    if (h.corporate()) {
                        var account = new pl.training.workshop.m5.s14_reuse.step1.CorporateAccount(h.owner());
                        h.payments().forEach(p -> account.earn(Money.of(p)));
                        return new pl.training.workshop.m5.s14_reuse.step1.LoyaltyReport().line(account);
                    }
                    var account = new pl.training.workshop.m5.s14_reuse.step1.LoyaltyAccount(h.owner());
                    h.payments().forEach(p -> account.earn(Money.of(p)));
                    return new pl.training.workshop.m5.s14_reuse.step1.LoyaltyReport().line(account);
                })
                .variant("step2", h -> {
                    if (h.corporate()) {
                        var account = new pl.training.workshop.m5.s14_reuse.step2.CorporateAccount(h.owner());
                        h.payments().forEach(p -> account.earn(Money.of(p)));
                        return new pl.training.workshop.m5.s14_reuse.step2.LoyaltyReport().line(account);
                    }
                    var account = new pl.training.workshop.m5.s14_reuse.step2.LoyaltyAccount(h.owner());
                    h.payments().forEach(p -> account.earn(Money.of(p)));
                    return new pl.training.workshop.m5.s14_reuse.step2.LoyaltyReport().line(account);
                })
                .expect("klient: pełne dziesiątki", new History(false, "anna@kino.pl", List.of("45.00", "32.00")),
                        "anna@kino.pl: 7 pkt")
                .expect("firma: duża transakcja", new History(true, "Kino-Tech", List.of("999.99")),
                        "Kino-Tech: 99 pkt")
                .expect("klient bez zakupów", new History(false, "jan@kino.pl", List.of()), "jan@kino.pl: 0 pkt")
                .tests();
    }
}
