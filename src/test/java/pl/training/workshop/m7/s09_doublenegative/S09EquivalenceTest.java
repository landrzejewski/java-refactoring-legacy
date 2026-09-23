package pl.training.workshop.m7.s09_doublenegative;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności. Wejście opisujemy "po staremu" (notVip), a adapter kroku 3
 * musi odwrócić wartość przy budowie rekordu - to jest dokładnie migracja granicy.
 */
final class S09EquivalenceTest {
    record Visit(boolean notVip, LocalDate voucherValidUntil) {
    }

    private static final LocalDate TODAY = LocalDate.of(2026, 3, 10);

    @TestFactory
    Stream<DynamicTest> everyStepGrantsTheSameAccess() {
        return Scene.<Visit, String>variants()
                .variant("start", v -> {
                    var customer = new pl.training.workshop.m7.s09_doublenegative.start.Customer("anna@kino.pl", v.notVip());
                    var voucher = v.voucherValidUntil() == null ? null
                            : new pl.training.workshop.m7.s09_doublenegative.start.Voucher("LOUNGE", v.voucherValidUntil());
                    var lounge = new pl.training.workshop.m7.s09_doublenegative.start.LoungeAccess();
                    return lounge.canEnter(customer, voucher, TODAY) + " " + lounge.badge(customer);
                })
                .variant("step1", v -> {
                    var customer = new pl.training.workshop.m7.s09_doublenegative.step1.Customer("anna@kino.pl", v.notVip());
                    var voucher = v.voucherValidUntil() == null ? null
                            : new pl.training.workshop.m7.s09_doublenegative.step1.Voucher("LOUNGE", v.voucherValidUntil());
                    var lounge = new pl.training.workshop.m7.s09_doublenegative.step1.LoungeAccess();
                    return lounge.canEnter(customer, voucher, TODAY) + " " + lounge.badge(customer);
                })
                .variant("step2", v -> {
                    var customer = new pl.training.workshop.m7.s09_doublenegative.step2.Customer("anna@kino.pl", v.notVip());
                    var voucher = v.voucherValidUntil() == null ? null
                            : new pl.training.workshop.m7.s09_doublenegative.step2.Voucher("LOUNGE", v.voucherValidUntil());
                    var lounge = new pl.training.workshop.m7.s09_doublenegative.step2.LoungeAccess();
                    return lounge.canEnter(customer, voucher, TODAY) + " " + lounge.badge(customer);
                })
                .variant("step3", v -> {
                    var customer = new pl.training.workshop.m7.s09_doublenegative.step3.Customer("anna@kino.pl", !v.notVip());
                    var voucher = v.voucherValidUntil() == null ? null
                            : new pl.training.workshop.m7.s09_doublenegative.step3.Voucher("LOUNGE", v.voucherValidUntil());
                    var lounge = new pl.training.workshop.m7.s09_doublenegative.step3.LoungeAccess();
                    return lounge.canEnter(customer, voucher, TODAY) + " " + lounge.badge(customer);
                })
                .expect("VIP bez vouchera", new Visit(false, null), "true VIP")
                .expect("VIP z przeterminowanym voucherem", new Visit(false, TODAY.minusDays(1)), "true VIP")
                .expect("zwykly klient bez vouchera", new Visit(true, null), "false STANDARD")
                .expect("voucher wazny do dzis wlacznie", new Visit(true, TODAY), "true STANDARD")
                .expect("voucher wygasl wczoraj", new Visit(true, TODAY.minusDays(1)), "false STANDARD")
                .tests();
    }
}
