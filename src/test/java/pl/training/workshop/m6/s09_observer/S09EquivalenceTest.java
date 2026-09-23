package pl.training.workshop.m6.s09_observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Kolejność powiadomień i polityka błędów (fail-fast) takie same w każdym kroku. */
final class S09EquivalenceTest {
    /** failing: "mail", "sms", "loyalty" albo "" - który odbiorca rzuca wyjątek. */
    record Case(Payment payment, String failing) {
    }

    record Ports(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepNotifiesTheSameWay() {
        return Scene.<Case, String>variants()
                .variant("start", c -> play(c, p -> {
                    var s = pl.training.workshop.m6.s09_observer.start.PaymentServices.standard(
                            p.mailer(), p.sms(), p.loyalty());
                    return service(s::confirm, s::paid);
                }))
                .variant("step1", c -> play(c, p -> {
                    var s = pl.training.workshop.m6.s09_observer.step1.PaymentServices.standard(
                            p.mailer(), p.sms(), p.loyalty());
                    return service(s::confirm, s::paid);
                }))
                .variant("step2", c -> play(c, p -> {
                    var s = pl.training.workshop.m6.s09_observer.step2.PaymentServices.standard(
                            p.mailer(), p.sms(), p.loyalty());
                    return service(s::confirm, s::paid);
                }))
                .variant("step3", c -> play(c, p -> {
                    var s = pl.training.workshop.m6.s09_observer.step3.PaymentServices.standard(
                            p.mailer(), p.sms(), p.loyalty());
                    return service(s::confirm, s::paid);
                }))
                .expect("wszyscy odbiorcy w kolejności", new Case(payment("95.50"), ""), """
                        mail anna@kino.pl: Potwierdzenie platnosci R1: 95.50
                        sms 600100200: Oplacono R1
                        points anna@kino.pl +9
                        paid=[R1]""")
                .expect("wyjątek w SMS: mail wysłany, punkty nie, opłata zapisana", new Case(payment("40.00"), "sms"), """
                        mail anna@kino.pl: Potwierdzenie platnosci R1: 40.00
                        ERROR sms down
                        paid=[R1]""")
                .expect("wyjątek w mailu przerywa wszystko", new Case(payment("40.00"), "mail"), """
                        ERROR mail down
                        paid=[R1]""")
                .expect("kwota niedodatnia - nikt nie dostaje powiadomienia", new Case(payment("0.00"), ""), """
                        ERROR amount must be positive
                        paid=[]""")
                .tests();
    }

    record Service(Consumer<Payment> confirm, Supplier<List<String>> paid) {
    }

    private static Service service(Consumer<Payment> confirm, Supplier<List<String>> paid) {
        return new Service(confirm, paid);
    }

    private static Payment payment(String amount) {
        return new Payment("R1", "anna@kino.pl", "600100200", Money.of(amount));
    }

    private static String play(Case c, Function<Ports, Service> factory) {
        List<String> log = new ArrayList<>();
        Ports ports = new Ports(
                (to, text) -> {
                    failIf(c, "mail");
                    log.add("mail " + to + ": " + text);
                },
                (phone, text) -> {
                    failIf(c, "sms");
                    log.add("sms " + phone + ": " + text);
                },
                (email, points) -> {
                    failIf(c, "loyalty");
                    log.add("points " + email + " +" + points);
                });
        Service service = factory.apply(ports);
        try {
            service.confirm().accept(c.payment());
        } catch (RuntimeException exception) {
            log.add("ERROR " + exception.getMessage());
        }
        log.add("paid=" + service.paid().get());
        return String.join("\n", log);
    }

    private static void failIf(Case c, String channel) {
        if (c.failing().equals(channel)) {
            throw new IllegalStateException(channel + " down");
        }
    }
}
