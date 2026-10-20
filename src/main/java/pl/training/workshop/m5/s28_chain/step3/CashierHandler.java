package pl.training.workshop.m5.s28_chain.step3;

import java.util.Objects;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/** Krok 3: poziom CASHIER - w limicie zatwierdza i kończy, powyżej eskaluje do następnika. */
public final class CashierHandler implements RefundHandler {
    private final Money limit;
    private final RefundHandler next;

    public CashierHandler(Money limit, RefundHandler next) {
        this.limit = Objects.requireNonNull(limit, "limit");
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public RefundDecision handle(RefundRequest request) {
        if (!request.amount().isGreaterThan(limit)) {
            return RefundDecision.approvedBy("CASHIER");
        }
        return next.handle(request);
    }
}
