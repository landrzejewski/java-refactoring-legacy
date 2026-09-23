package pl.training.workshop.m6.s14_adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s14_adapter.step3.CheckoutService;
import pl.training.workshop.m6.s14_adapter.step3.PaymentGateway;
import pl.training.workshop.m6.s14_adapter.step3.XmlPayAdapter;
import pl.training.workshop.shared.Money;

/** Adapter tłumaczy jednostki; serwis da się testować bez żadnej bramki. */
final class S14SolutionTest {
    @Test
    void xmlAdapterSendsAmountInGrosze() {
        List<String> sent = new ArrayList<>();
        XmlPayGateway recording = new XmlPayGateway() {
            @Override
            public String submit(String xml) {
                sent.add(xml);
                return super.submit(xml);
            }
        };
        new XmlPayAdapter(recording).pay("R1", Money.of("40.00"));
        assertEquals(List.of("<charge ref='R1' amount='4000'/>"), sent);
    }

    @Test
    void checkoutWorksWithAnyGateway() {
        PaymentGateway fake = (reservationId, amount) -> PaymentResult.accepted("FAKE-" + reservationId);
        assertEquals("FAKE-R9", new CheckoutService(Map.of("FAKE", fake))
                .pay("FAKE", "R9", Money.of("25.00")).transactionId());
    }
}
