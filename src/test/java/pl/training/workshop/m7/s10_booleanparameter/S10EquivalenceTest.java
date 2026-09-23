package pl.training.workshop.m7.s10_booleanparameter;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Równoważność obserwowana przez klientów (wszystkie warianty) oraz przez stare API
 * (dopóki istnieje, czyli start..step3). Wejście: tytuł, format, liczba miejsc, okulary klienta.
 */
final class S10EquivalenceTest {
    record Sale(String title, String format, int seats, boolean flagA, boolean flagB) {
    }

    @TestFactory
    Stream<DynamicTest> clientsSeeTheSamePrices() {
        return Scene.<Sale, String>variants()
                .variant("start", s -> {
                    var service = new pl.training.workshop.m7.s10_booleanparameter.start.TicketService();
                    return new pl.training.workshop.m7.s10_booleanparameter.start.MobileApp(service)
                            .buy(s.title(), s.format(), s.seats())
                            + " | " + new pl.training.workshop.m7.s10_booleanparameter.start.BoxOfficeTerminal(service)
                            .sell(s.title(), s.format(), s.seats(), s.flagA());
                })
                .variant("step1", s -> {
                    var service = new pl.training.workshop.m7.s10_booleanparameter.step1.TicketService();
                    return new pl.training.workshop.m7.s10_booleanparameter.step1.MobileApp(service)
                            .buy(s.title(), s.format(), s.seats())
                            + " | " + new pl.training.workshop.m7.s10_booleanparameter.step1.BoxOfficeTerminal(service)
                            .sell(s.title(), s.format(), s.seats(), s.flagA());
                })
                .variant("step2", s -> {
                    var service = new pl.training.workshop.m7.s10_booleanparameter.step2.TicketService();
                    return new pl.training.workshop.m7.s10_booleanparameter.step2.MobileApp(service)
                            .buy(s.title(), s.format(), s.seats())
                            + " | " + new pl.training.workshop.m7.s10_booleanparameter.step2.BoxOfficeTerminal(service)
                            .sell(s.title(), s.format(), s.seats(), s.flagA());
                })
                .variant("step3", s -> {
                    var service = new pl.training.workshop.m7.s10_booleanparameter.step3.TicketService();
                    return new pl.training.workshop.m7.s10_booleanparameter.step3.MobileApp(service)
                            .buy(s.title(), s.format(), s.seats())
                            + " | " + new pl.training.workshop.m7.s10_booleanparameter.step3.BoxOfficeTerminal(service)
                            .sell(s.title(), s.format(), s.seats(), s.flagA());
                })
                .variant("step4", s -> {
                    var service = new pl.training.workshop.m7.s10_booleanparameter.step4.TicketService();
                    return new pl.training.workshop.m7.s10_booleanparameter.step4.MobileApp(service)
                            .buy(s.title(), s.format(), s.seats())
                            + " | " + new pl.training.workshop.m7.s10_booleanparameter.step4.BoxOfficeTerminal(service)
                            .sell(s.title(), s.format(), s.seats(), s.flagA());
                })
                .expect("IMAX x2", new Sale("Diuna", "IMAX", 2, false, false),
                        "Diuna IMAX x2 online: 84.00 | Diuna IMAX x2 kasa: 80.00")
                .expect("3D x2, klient w kasie ma okulary", new Sale("Kraina Lodu", "3D", 2, true, false),
                        "Kraina Lodu 3D x2 online: 74.00 | Kraina Lodu 3D x2 kasa: 64.00")
                .expect("3D x1, klient w kasie bez okularow", new Sale("Kraina Lodu", "3D", 1, false, false),
                        "Kraina Lodu 3D x1 online: 37.00 | Kraina Lodu 3D x1 kasa: 35.00")
                .tests();
    }

    @TestFactory
    @SuppressWarnings("deprecation")
    Stream<DynamicTest> oldFlagApiStillWorksDuringMigration() {
        var start = new pl.training.workshop.m7.s10_booleanparameter.start.TicketService();
        var step1 = new pl.training.workshop.m7.s10_booleanparameter.step1.TicketService();
        var step2 = new pl.training.workshop.m7.s10_booleanparameter.step2.TicketService();
        var step3 = new pl.training.workshop.m7.s10_booleanparameter.step3.TicketService();
        var scene = Scene.<Sale, String>variants()
                .variant("start", s -> start.book(s.title(), s.format(), s.seats(), s.flagA(), s.flagB()))
                .variant("step1", s -> step1.book(s.title(), s.format(), s.seats(), s.flagA(), s.flagB()))
                .variant("step2", s -> step2.book(s.title(), s.format(), s.seats(), s.flagA(), s.flagB()))
                .variant("step3", s -> step3.book(s.title(), s.format(), s.seats(), s.flagA(), s.flagB()));
        for (boolean online : List.of(true, false)) {
            for (boolean ownGlasses : List.of(true, false)) {
                int total = 64 + (ownGlasses ? 0 : 6) + (online ? 4 : 0);
                scene.expect("online=" + online + ", ownGlasses=" + ownGlasses,
                        new Sale("Kraina Lodu", "3D", 2, online, ownGlasses),
                        "Kraina Lodu 3D x2 " + (online ? "online" : "kasa") + ": " + total + ".00");
            }
        }
        return scene.tests();
    }
}
