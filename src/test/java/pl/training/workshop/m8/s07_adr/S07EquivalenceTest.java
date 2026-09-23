package pl.training.workshop.m8.s07_adr;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: dochodzenie do zgodności z ADR nie zmienia odpowiedzi ani wysłanych maili. */
final class S07EquivalenceTest {
    record Order(String organizer, int tickets, String format) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepBooksTheSameWay() {
        return Scene.<Order, String>variants()
                .variant("start", order -> {
                    var service = new pl.training.workshop.m8.s07_adr.start.BookingService();
                    return service.book(order.organizer(), order.tickets(), order.format()) + " " + service.sentMails();
                })
                .variant("step1", order -> {
                    var service = new pl.training.workshop.m8.s07_adr.step1.BookingService();
                    return service.book(order.organizer(), order.tickets(), order.format()) + " " + service.sentMails();
                })
                .variant("step2", order -> {
                    var service = new pl.training.workshop.m8.s07_adr.step2.BookingService();
                    return service.book(order.organizer(), order.tickets(), order.format()) + " " + service.sentMails();
                })
                .expect("grupa 12 biletów 2D", new Order("anna@kino.pl", 12, "2D"),
                        "DO ZAPLATY 270.00 [anna@kino.pl: rabat grupowy dla 12 biletow]")
                .expect("2 bilety IMAX", new Order("jan@kino.pl", 2, "IMAX"), "DO ZAPLATY 80.00 []")
                .expect("grupa 10 biletów 3D", new Order("ola@kino.pl", 10, "3D"),
                        "DO ZAPLATY 288.00 [ola@kino.pl: rabat grupowy dla 10 biletow]")
                .tests();
    }
}
