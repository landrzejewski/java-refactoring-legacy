package pl.training.workshop.m4.s12_encapsulateconditional;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Test równoważności: ta sama kwota zwrotu - oba boki progu 24 h, start seansu, promocje, null. */
final class S12EquivalenceTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 25, 20, 0);
    private static final Money SEVENTY = Money.of("70.00");

    record Refund(Booking booking, LocalDateTime now) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepRefundsTheSameAmount() {
        return Scene.<Refund, String>variants()
                .variant("start", r -> new pl.training.workshop.m4.s12_encapsulateconditional.start
                        .RefundCalculator().refund(r.booking(), r.now()).toString())
                .variant("step1", r -> new pl.training.workshop.m4.s12_encapsulateconditional.step1
                        .RefundCalculator().refund(r.booking(), r.now()).toString())
                .variant("step2", r -> new pl.training.workshop.m4.s12_encapsulateconditional.step2
                        .RefundCalculator().refund(r.booking(), r.now()).toString())
                .variant("step3", r -> new pl.training.workshop.m4.s12_encapsulateconditional.step3
                        .RefundCalculator().refund(r.booking(), r.now()).toString())
                .expect("dokładnie 24 h przed - 100% minus 3.00",
                        new Refund(new Booking("PAID", START, SEVENTY, null), START.minusHours(24)), "67.00")
                .expect("24 h minus minuta - 50% minus 3.00",
                        new Refund(new Booking("PAID", START, SEVENTY, null), START.minusHours(24).plusMinutes(1)),
                        "32.00")
                .expect("w chwili startu - brak zwrotu",
                        new Refund(new Booking("PAID", START, SEVENTY, null), START), "0.00")
                .expect("nieopłacona - brak zwrotu",
                        new Refund(new Booking("NEW", START, SEVENTY, null), START.minusDays(3)), "0.00")
                .expect("darmowy bilet z promocji - brak zwrotu",
                        new Refund(new Booking("PAID", START, SEVENTY, "FREE-100"), START.minusDays(3)), "0.00")
                .expect("inna promocja - zwrot jak zwykle",
                        new Refund(new Booking("PAID", START, SEVENTY, "STUDENT"), START.minusDays(3)), "67.00")
                .expect("tani bilet: 50% z 5.00 minus 3.00 nie schodzi poniżej zera",
                        new Refund(new Booking("PAID", START, Money.of("5.00"), null), START.minusHours(2)), "0.00")
                .tests();
    }
}
