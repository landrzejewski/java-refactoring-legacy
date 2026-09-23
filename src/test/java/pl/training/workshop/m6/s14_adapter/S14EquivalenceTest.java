package pl.training.workshop.m6.s14_adapter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Wejście: [dostawca, rezerwacja, kwota]. Obie bramki, sukces i odmowa, nieznany dostawca. */
final class S14EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPaysTheSameWay() {
        XmlPayGateway xml = new XmlPayGateway();
        RestPayClient rest = new RestPayClient();
        return Scene.<List<String>, String>variants()
                .variant("start", safe(new pl.training.workshop.m6.s14_adapter.start.CheckoutService(xml, rest)::pay))
                .variant("step1", safe(new pl.training.workshop.m6.s14_adapter.step1.CheckoutService(xml, rest)::pay))
                .variant("step2", safe(new pl.training.workshop.m6.s14_adapter.step2.CheckoutService(xml, rest)::pay))
                .variant("step3", safe(new pl.training.workshop.m6.s14_adapter.step3.CheckoutService(xml, rest)::pay))
                .expect("XML sukces", List.of("XML", "R1", "40.00"), "OK X-R1")
                .expect("XML grosze", List.of("XML", "R2", "0.50"), "OK X-R2")
                .expect("XML odmowa", List.of("XML", "R3", "600.00"), "DECLINED 51")
                .expect("REST sukces", List.of("REST", "R4", "40.00"), "OK T-R4")
                .expect("REST odmowa jako wyjątek biblioteki", List.of("REST", "R5", "600.00"), "DECLINED LIMIT")
                .expect("granica 500.00 włącznie", List.of("REST", "R6", "500.00"), "OK T-R6")
                .expect("nieznany dostawca", List.of("SWIFT", "R7", "40.00"), "ERROR unknown provider: SWIFT")
                .tests();
    }

    interface Pay {
        PaymentResult pay(String provider, String reservationId, Money amount);
    }

    private static Function<List<String>, String> safe(Pay pay) {
        return input -> {
            try {
                PaymentResult result = pay.pay(input.get(0), input.get(1), Money.of(input.get(2)));
                return result.accepted() ? "OK " + result.transactionId() : "DECLINED " + result.declineCode();
            } catch (IllegalArgumentException exception) {
                return "ERROR " + exception.getMessage();
            }
        };
    }
}
