package pl.training.workshop.m4.s02_extractvariable;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: start i każdy krok liczą tę samą cenę biletu. */
final class S02EquivalenceTest {
    private static final TicketRequest FREE_SEATING = new TicketRequest(2, "E", LocalTime.of(18, 0), null, true);

    @TestFactory
    Stream<DynamicTest> everyStepPricesTicketsTheSameWay() {
        return Scene.<TicketRequest, String>variants()
                .variant("start", r -> new pl.training.workshop.m4.s02_extractvariable.start.TicketPrice()
                        .price(r).toPlainString())
                .variant("step1", r -> new pl.training.workshop.m4.s02_extractvariable.step1.TicketPrice()
                        .price(r).toPlainString())
                .variant("step2", r -> new pl.training.workshop.m4.s02_extractvariable.step2.TicketPrice()
                        .price(r).toPlainString())
                .variant("step3", r -> new pl.training.workshop.m4.s02_extractvariable.step3.TicketPrice()
                        .price(r).toPlainString())
                .expect("IMAX normalny wieczorem",
                        new TicketRequest(3, "N", LocalTime.of(20, 0), 5, false), "40.00")
                .expect("3D student rano, VIP, bez okularów",
                        new TicketRequest(2, "S", LocalTime.of(10, 0), 12, false), "32.00")
                .expect("3D senior, wolna widownia (row = null), własne okulary", FREE_SEATING, "22.40")
                .expect("2D dziecko 11:59, rząd 10 to już VIP",
                        new TicketRequest(1, "C", LocalTime.of(11, 59), 10, false), "20.00")
                .expect("3D normalny 12:00 to już nie poranek, rząd 9",
                        new TicketRequest(2, "N", LocalTime.of(12, 0), 9, false), "35.00")
                .expect("IMAX dziecko rano, wolna widownia",
                        new TicketRequest(3, "C", LocalTime.of(9, 0), null, false), "19.00")
                .tests();
    }

    /** Dokumentuje pułapkę: wydzielenie samego porównania bez osłony null zmienia zachowanie. */
    @Test
    void extractingTheComparisonWithoutTheNullGuardThrows() {
        assertThrows(NullPointerException.class, () -> {
            boolean vipSeat = FREE_SEATING.row() >= 10;
            boolean hasSeat = FREE_SEATING.row() != null;
            if (hasSeat && vipSeat) {
                throw new AssertionError("nie powinno tu dojść");
            }
        });
    }
}
