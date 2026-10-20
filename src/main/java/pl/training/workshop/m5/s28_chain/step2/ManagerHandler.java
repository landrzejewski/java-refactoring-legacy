package pl.training.workshop.m5.s28_chain.step2;

import java.util.Objects;
import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/** Krok 2: poziom MANAGER zatwierdza zwroty do swojego limitu (włącznie). */
public final class ManagerHandler implements RefundHandler {
    private final Money limit;

    public ManagerHandler(Money limit) {
        this.limit = Objects.requireNonNull(limit, "limit");
    }

    @Override
    public Optional<RefundDecision> handle(RefundRequest request) {
        if (!request.amount().isGreaterThan(limit)) {
            return Optional.of(RefundDecision.approvedBy("MANAGER"));
        }
        return Optional.empty();
    }
}
