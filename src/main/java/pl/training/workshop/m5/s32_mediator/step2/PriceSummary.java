package pl.training.workshop.m5.s32_mediator.step2;

import java.util.List;
import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: podsumowanie zna tylko mediatora. Liczy cenę z miejsc, które poda mu mediator
 * (rząd C - VIP 35.00, pozostałe 25.00, voucher KINO10 to -10%).
 */
public final class PriceSummary {
    private int discountPercent;
    private Money total = Money.ZERO;
    private final SelectionMediator mediator;

    public PriceSummary(SelectionMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    /** Akcja użytkownika: voucher; nieznany kod usuwa zniżkę. */
    public void applyVoucher(String code) {
        discountPercent = code.equals("KINO10") ? 10 : 0;
        mediator.voucherApplied();
    }

    void recalculate(List<String> seats) {
        Money sum = Money.ZERO;
        for (String seat : seats) {
            sum = sum.plus(seat.startsWith("C") ? Money.of("35.00") : Money.of("25.00"));
        }
        total = sum.minus(sum.percent(discountPercent));
    }

    Money total() {
        return total;
    }

    String describe() {
        return "suma=" + total;
    }
}
