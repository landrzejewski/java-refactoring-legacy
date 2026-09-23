package pl.training.workshop.m3.s02_similarity;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Rozdzielenie fałszywie scalonych opłat nie zmienia żadnej kwoty. */
final class S02EquivalenceTest {
    record Case(List<BigDecimal> ticketPrices, BigDecimal paid, int refundPercent) {
    }

    private static Case of(String paid, int percent, String... prices) {
        return new Case(Stream.of(prices).map(BigDecimal::new).toList(), new BigDecimal(paid), percent);
    }

    @TestFactory
    Stream<DynamicTest> everyStepChargesAndRefundsTheSame() {
        return Scene.<Case, String>variants()
                .variant("start", c -> "online " + new pl.training.workshop.m3.s02_similarity.start.OnlineCheckout()
                        .total(c.ticketPrices()) + " / zwrot " + new pl.training.workshop.m3.s02_similarity.start
                        .RefundDesk().refund(c.paid(), c.refundPercent()))
                .variant("step1", c -> "online " + new pl.training.workshop.m3.s02_similarity.step1.OnlineCheckout()
                        .total(c.ticketPrices()) + " / zwrot " + new pl.training.workshop.m3.s02_similarity.step1
                        .RefundDesk().refund(c.paid(), c.refundPercent()))
                .variant("step2", c -> "online " + new pl.training.workshop.m3.s02_similarity.step2.OnlineCheckout()
                        .total(c.ticketPrices()) + " / zwrot " + new pl.training.workshop.m3.s02_similarity.step2
                        .RefundDesk().refund(c.paid(), c.refundPercent()))
                .variant("step3", c -> "online " + new pl.training.workshop.m3.s02_similarity.step3.OnlineCheckout()
                        .total(c.ticketPrices()) + " / zwrot " + new pl.training.workshop.m3.s02_similarity.step3
                        .RefundDesk().refund(c.paid(), c.refundPercent()))
                .expect("dwa bilety, pelny zwrot", of("65.00", 100, "40.00", "25.00"),
                        "online 69.00 / zwrot 62.00")
                .expect("jeden bilet, zwrot 50%", of("19.00", 50, "19.00"),
                        "online 21.00 / zwrot 6.50")
                .expect("zwrot po starcie nie schodzi ponizej zera", of("17.50", 0, "17.50"),
                        "online 19.50 / zwrot 0.00")
                .expect("potracenie wieksze niz polowa ceny", of("5.00", 50, "25.00", "25.00", "25.00"),
                        "online 81.00 / zwrot 0.00")
                .tests();
    }
}
