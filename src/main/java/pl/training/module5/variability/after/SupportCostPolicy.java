package pl.training.module5.variability.after;

import java.util.Objects;

import pl.training.module5.variability.CustomerTier;

/**
 * Strategy: algorytm kosztu wsparcia. Wybierany raz przez klienta, potem tylko wywoływany.
 */
@FunctionalInterface
public interface SupportCostPolicy {
    SupportCostPolicy STANDARD = hours -> hours * 100L;
    SupportCostPolicy PREMIUM = hours -> 500 + hours * 80L;
    SupportCostPolicy ENTERPRISE = hours -> Math.max(2_000, hours * 60L);

    long cost(int hours);

    static SupportCostPolicy forTier(CustomerTier tier) {
        return switch (Objects.requireNonNull(tier, "tier")) {
            case STANDARD -> STANDARD;
            case PREMIUM -> PREMIUM;
            case ENTERPRISE -> ENTERPRISE;
        };
    }
}
