package pl.training.workshop.m7.s15_behaviourvector;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Słaby test z punktu startu: obserwuje tylko wynik. Jest zielony dla KAŻDEGO wariantu -
 * także dla start i step1, które wysyłają "Bilety oplacone" po odrzuceniu karty.
 */
final class S15ResultOnlyTest {
    @TestFactory
    Stream<DynamicTest> resultOnlyCannotSeeTheRegression() {
        return Scene.<Payment, String>variants()
                .variant("start", p -> new pl.training.workshop.m7.s15_behaviourvector.start.TicketCheckout()
                        .pay(p.booking(), p.card()))
                .variant("step1", p -> new pl.training.workshop.m7.s15_behaviourvector.step1.TicketCheckout()
                        .pay(p.booking(), p.card()))
                .variant("step2", p -> new pl.training.workshop.m7.s15_behaviourvector.step2.TicketCheckout()
                        .pay(p.booking(), p.card()))
                .variant("step3", p -> new pl.training.workshop.m7.s15_behaviourvector.step3.TicketCheckout()
                        .pay(p.booking(), p.card()))
                .expect("sukces", Payment.SUCCESS, "OK")
                .expect("karta odrzucona", Payment.DECLINED, "DECLINED")
                .expect("juz oplacona", Payment.ALREADY_PAID, "ERROR: status PAID")
                .tests();
    }
}
