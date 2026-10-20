package pl.training.workshop.m5.s32_mediator.step1;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - podsumowanie ceny - rząd C (VIP) 35.00, pozostałe 25.00, voucher KINO10 to -10%.
 * Po wpisaniu vouchera samo pyta koszyk o miejsca i samo odświeża przycisk.
 */
public final class PriceSummary {
    private int discountPercent;
    private Money total = Money.ZERO;
    private Basket basket;
    private PayButton payButton;

    void connect(Basket basket, PayButton payButton) {
        this.basket = basket;
        this.payButton = payButton;
    }

    /** Akcja użytkownika: voucher; nieznany kod usuwa zniżkę. */
    public void applyVoucher(String code) {
        discountPercent = code.equals("KINO10") ? 10 : 0;
        recalculate(basket.seats());
        payButton.refresh(basket.seats().size(), total);
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
