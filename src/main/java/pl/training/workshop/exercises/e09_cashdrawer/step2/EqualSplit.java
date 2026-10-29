package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: równe części zaokrąglone w dół; brakujące grosze dostają po jednym pierwsze bilety.
 * Bezstanowy.
 */
public final class EqualSplit implements DiscountSplit {
    private static final Money CENT = Money.of("0.01");

    @Override
    public List<Money> split(Money discount, List<Money> tickets) {
        Money base = new Money(discount.amount()
                .divide(BigDecimal.valueOf(tickets.size()), 2, RoundingMode.DOWN));
        int restCents = discount.minus(base.times(tickets.size())).amount().movePointRight(2).intValueExact();
        List<Money> shares = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            shares.add(i < restCents ? base.plus(CENT) : base);
        }
        return shares;
    }
}
