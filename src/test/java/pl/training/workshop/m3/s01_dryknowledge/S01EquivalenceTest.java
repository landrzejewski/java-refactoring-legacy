package pl.training.workshop.m3.s01_dryknowledge;

import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Start i każdy krok: ta sama cena sprzedaży i ta sama kwota zwrotu. */
final class S01EquivalenceTest {
    record Case(Ticket ticket, long hoursBeforeStart) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepSellsAndRefundsTheSame() {
        return Scene.<Case, String>variants()
                .variant("start", c -> {
                    var office = new pl.training.workshop.m3.s01_dryknowledge.start.BoxOffice();
                    return office.sell(c.ticket()) + " / " + office.refund(c.ticket(), c.hoursBeforeStart());
                })
                .variant("step1", c -> {
                    var office = new pl.training.workshop.m3.s01_dryknowledge.step1.BoxOffice();
                    return office.sell(c.ticket()) + " / " + office.refund(c.ticket(), c.hoursBeforeStart());
                })
                .variant("step2", c -> {
                    var office = new pl.training.workshop.m3.s01_dryknowledge.step2.BoxOffice();
                    return office.sell(c.ticket()) + " / " + office.refund(c.ticket(), c.hoursBeforeStart());
                })
                .variant("step3", c -> {
                    var office = new pl.training.workshop.m3.s01_dryknowledge.step3.BoxOffice();
                    return office.sell(c.ticket()) + " / " + office.refund(c.ticket(), c.hoursBeforeStart());
                })
                .variant("step4", c -> {
                    var office = new pl.training.workshop.m3.s01_dryknowledge.step4.BoxOffice();
                    return office.sell(c.ticket()) + " / " + office.refund(c.ticket(), c.hoursBeforeStart());
                })
                .expect("IMAX normalny, zwrot 48h przed: 100% - 3.00",
                        new Case(new Ticket("IMAX", "NORMAL", LocalTime.of(20, 0)), 48), "40.00 / 37.00")
                .expect("3D student rano, zwrot 10h przed: 50% - 3.00",
                        new Case(new Ticket("3D", "STUDENT", LocalTime.of(11, 0)), 10), "19.00 / 6.50")
                .expect("2D senior, zwrot po starcie: 0, nie ponizej zera",
                        new Case(new Ticket("2D", "SENIOR", LocalTime.of(18, 0)), 0), "17.50 / 0.00")
                .expect("2D dziecko rano, zwrot dokladnie 24h przed",
                        new Case(new Ticket("2D", "CHILD", LocalTime.of(10, 0)), 24), "10.00 / 7.00")
                .expect("3D senior wieczorem, zwrot 1h przed",
                        new Case(new Ticket("3D", "SENIOR", LocalTime.of(21, 15)), 1), "22.40 / 8.20")
                .tests();
    }
}
