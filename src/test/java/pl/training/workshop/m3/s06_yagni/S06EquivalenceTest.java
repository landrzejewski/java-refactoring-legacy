package pl.training.workshop.m3.s06_yagni;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Silnik reguł i dwa proste warunki dają te same ceny. */
final class S06EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesTheSame() {
        return Scene.<TicketQuote, String>variants()
                .variant("start", q -> new pl.training.workshop.m3.s06_yagni.start.TicketPricer().price(q).toString())
                .variant("step1", q -> new pl.training.workshop.m3.s06_yagni.step1.TicketPricer().price(q).toString())
                .variant("step2", q -> new pl.training.workshop.m3.s06_yagni.step2.TicketPricer().price(q).toString())
                .variant("step3", q -> new pl.training.workshop.m3.s06_yagni.step3.TicketPricer().price(q).toString())
                .expect("IMAX wieczorem, zwykle miejsce", new TicketQuote("IMAX", LocalTime.of(20, 0), 5, 10), "40.00")
                .expect("3D rano, VIP", new TicketQuote("3D", LocalTime.of(10, 0), 12, 10), "37.00")
                .expect("2D 11:59 to jeszcze poranek, pierwszy rzad VIP",
                        new TicketQuote("2D", LocalTime.of(11, 59), 10, 10), "30.00")
                .expect("2D 12:00 to juz nie poranek", new TicketQuote("2D", LocalTime.of(12, 0), 9, 10), "25.00")
                .tests();
    }
}
