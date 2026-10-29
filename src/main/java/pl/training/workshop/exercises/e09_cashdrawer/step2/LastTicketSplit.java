package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 2: cały rabat na ostatnim bilecie - bez zaokrągleń. Bezstanowy. */
public final class LastTicketSplit implements DiscountSplit {
    @Override
    public List<Money> split(Money discount, List<Money> tickets) {
        List<Money> shares = new ArrayList<>();
        for (int i = 0; i < tickets.size() - 1; i++) {
            shares.add(Money.ZERO);
        }
        shares.add(discount);
        return shares;
    }
}
