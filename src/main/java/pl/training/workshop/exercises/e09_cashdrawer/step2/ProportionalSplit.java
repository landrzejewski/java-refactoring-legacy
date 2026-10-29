package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: udział proporcjonalny do ceny biletu, zaokrąglony HALF_UP; różnica zaokrągleń
 * trafia na ostatni bilet, żeby suma zgadzała się co do grosza. Bezstanowy.
 */
public final class ProportionalSplit implements DiscountSplit {
    @Override
    public List<Money> split(Money discount, List<Money> tickets) {
        Money sum = Money.ZERO;
        for (Money ticket : tickets) {
            sum = sum.plus(ticket);
        }
        List<Money> shares = new ArrayList<>();
        Money given = Money.ZERO;
        for (int i = 0; i < tickets.size() - 1; i++) {
            Money share = new Money(discount.amount().multiply(tickets.get(i).amount())
                    .divide(sum.amount(), 2, RoundingMode.HALF_UP));
            shares.add(share);
            given = given.plus(share);
        }
        shares.add(discount.minus(given));
        return shares;
    }
}
