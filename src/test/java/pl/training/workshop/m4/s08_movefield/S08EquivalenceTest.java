package pl.training.workshop.m4.s08_movefield;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: ta sama wycena miejsca. Adaptery start/step1 podają próg do Screening,
 * a step2/step3 do Hall - to widoczna w teście zmiana konstruktorów po Move Field.
 */
final class S08EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepQuotesSeatsTheSameWay() {
        return Scene.<SeatQuery, String>variants()
                .variant("start", q -> new pl.training.workshop.m4.s08_movefield.start.SeatPricer().quote(
                        new pl.training.workshop.m4.s08_movefield.start.Screening(
                                new pl.training.workshop.m4.s08_movefield.start.Hall(q.hall()),
                                q.format(), q.vipFromRow()), q.row()))
                .variant("step1", q -> new pl.training.workshop.m4.s08_movefield.step1.SeatPricer().quote(
                        new pl.training.workshop.m4.s08_movefield.step1.Screening(
                                new pl.training.workshop.m4.s08_movefield.step1.Hall(q.hall()),
                                q.format(), q.vipFromRow()), q.row()))
                .variant("step2", q -> new pl.training.workshop.m4.s08_movefield.step2.SeatPricer().quote(
                        new pl.training.workshop.m4.s08_movefield.step2.Screening(
                                new pl.training.workshop.m4.s08_movefield.step2.Hall(q.hall(), q.vipFromRow()),
                                q.format()), q.row()))
                .variant("step3", q -> new pl.training.workshop.m4.s08_movefield.step3.SeatPricer().quote(
                        new pl.training.workshop.m4.s08_movefield.step3.Screening(
                                new pl.training.workshop.m4.s08_movefield.step3.Hall(q.hall(), q.vipFromRow()),
                                q.format()), q.row()))
                .expect("IMAX, rząd 10 przy progu 10 - VIP", new SeatQuery("Sala 1", 10, 3, 10),
                        "Sala 1, rzad 10 (VIP): 50.00")
                .expect("3D, rząd 9 przy progu 10", new SeatQuery("Sala 1", 10, 2, 9), "Sala 1, rzad 9: 32.00")
                .expect("2D, mała sala z progiem 8 - rząd 9 to VIP", new SeatQuery("Sala 2", 8, 1, 9),
                        "Sala 2, rzad 9 (VIP): 35.00")
                .tests();
    }
}
