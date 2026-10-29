package pl.training.workshop.exercises.e09_cashdrawer.step2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: reguła rabatu grupowego (10+ biletów = -10% sumy) jako kontekst, który podział
 * rabatu deleguje do wstrzykniętego wariantu. Kontekst nie zna nazw trybów.
 */
public final class GroupDiscount {
    private static final int GROUP_SIZE = 10;
    private static final int GROUP_PERCENT = 10;

    private final DiscountSplit split;

    public GroupDiscount(DiscountSplit split) {
        this.split = Objects.requireNonNull(split, "split");
    }

    /** Rabat i jego podział na bilety; pusty wynik, gdy grupa jest za mała. */
    public Optional<Allocation> allocate(List<Money> tickets) {
        if (tickets.size() < GROUP_SIZE) {
            return Optional.empty();
        }
        Money sum = Money.ZERO;
        for (Money ticket : tickets) {
            sum = sum.plus(ticket);
        }
        Money discount = sum.percent(GROUP_PERCENT);
        return Optional.of(new Allocation(discount, split.split(discount, tickets)));
    }

    public record Allocation(Money discount, List<Money> shares) {
    }
}
