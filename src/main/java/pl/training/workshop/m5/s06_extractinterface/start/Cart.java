package pl.training.workshop.m5.s06_extractinterface.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Start: koszyk ma dwie listy, dwa przeciążenia add(...) i dwie kopie liczenia VAT.
 * Każdy nowy rodzaj pozycji (np. okulary 3D) to kolejna lista i kolejna pętla.
 */
public final class Cart {
    private final List<Ticket> tickets = new ArrayList<>();
    private final List<Snack> snacks = new ArrayList<>();

    public void add(Ticket ticket) {
        tickets.add(ticket);
    }

    public void add(Snack snack) {
        snacks.add(snack);
    }

    public String summary() {
        Money total = Money.ZERO;
        Money vat = Money.ZERO;
        for (Ticket ticket : tickets) {
            total = total.plus(ticket.price());
            BigDecimal rate = BigDecimal.valueOf(ticket.vatPercent());
            vat = vat.plus(new Money(ticket.price().amount().multiply(rate)
                    .divide(rate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP)));
        }
        for (Snack snack : snacks) {
            total = total.plus(snack.price());
            BigDecimal rate = BigDecimal.valueOf(snack.vatPercent());
            vat = vat.plus(new Money(snack.price().amount().multiply(rate)
                    .divide(rate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP)));
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
