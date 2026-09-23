package pl.training.workshop.m7.s02_methodobject;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: start i każdy krok wyceniają zamówienia identycznie. */
final class S02EquivalenceTest {
    private static final LocalTime EVENING = LocalTime.of(20, 0);
    private static final LocalTime MORNING = LocalTime.of(11, 0);

    @TestFactory
    Stream<DynamicTest> everyStepQuotesTheSame() {
        return Scene.<GroupOrder, Quote>variants()
                .variant("start", new pl.training.workshop.m7.s02_methodobject.start.GroupPricing()::quote)
                .variant("step1", new pl.training.workshop.m7.s02_methodobject.step1.GroupPricing()::quote)
                .variant("step2", new pl.training.workshop.m7.s02_methodobject.step2.GroupPricing()::quote)
                .variant("step3", new pl.training.workshop.m7.s02_methodobject.step3.GroupPricing()::quote)
                .expect("IMAX wieczorem N+S+E, jedno VIP, online",
                        new GroupOrder("IMAX", EVENING, List.of("NORMAL", "STUDENT", "SENIOR"), 1, false, true),
                        quote("108.00", "6.00", "114.00", 10))
                .expect("3D rano, dwoje dzieci, okulary z kina, VIP, kasa",
                        new GroupOrder("3D", MORNING, List.of("NORMAL", "CHILD", "CHILD", "NORMAL"), 1, false, false),
                        quote("104.40", "0.00", "104.40", 10))
                .expect("grupa szkolna 2D: 8 dzieci + 2 opiekunow, online",
                        new GroupOrder("2D", LocalTime.of(18, 30),
                                List.of("CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD", "CHILD",
                                        "NORMAL", "NORMAL"), 0, false, true),
                        quote("153.00", "20.00", "173.00", 15))
                .expect("grupa 2D: rabat 10% z zaokragleniem (231.25 -> 208.12)",
                        new GroupOrder("2D", EVENING,
                                List.of("STUDENT", "STUDENT", "STUDENT", "NORMAL", "NORMAL", "NORMAL", "NORMAL",
                                        "NORMAL", "NORMAL", "NORMAL"), 0, false, false),
                        quote("208.12", "0.00", "208.12", 20))
                .expect("3D z wlasnymi okularami",
                        new GroupOrder("3D", EVENING, List.of("NORMAL"), 0, true, false),
                        quote("32.00", "0.00", "32.00", 3))
                .expect("puste zamowienie",
                        new GroupOrder("2D", EVENING, List.of(), 0, false, true),
                        quote("0.00", "0.00", "0.00", 0))
                .tests();
    }

    private static Quote quote(String tickets, String fees, String total, int points) {
        return new Quote(Money.of(tickets), Money.of(fees), Money.of(total), points);
    }
}
