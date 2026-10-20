package pl.training.workshop.m5.s28_chain.step3;

import java.util.Objects;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: jawne składanie łańcucha. Domyślny konstruktor buduje standardową ścieżkę, drugi
 * przyjmuje dowolną głowę łańcucha (np. festiwal bez zwrotów automatycznych). Walidacja
 * kwoty zostaje tutaj: to warunek wstępny, a nie poziom zatwierdzania.
 */
public final class RefundApprover {
    private static final Money CASHIER_LIMIT = Money.of("100.00");
    private static final Money MANAGER_LIMIT = Money.of("1000.00");

    private final RefundHandler chain;

    public RefundApprover() {
        this(new AfterScreeningHandler(
                new AutoRefundHandler(
                        new CashierHandler(CASHIER_LIMIT,
                                new ManagerHandler(MANAGER_LIMIT,
                                        new NoApproverHandler())))));
    }

    public RefundApprover(RefundHandler chain) {
        this.chain = Objects.requireNonNull(chain, "chain");
    }

    public RefundDecision decide(RefundRequest request) {
        if (!request.amount().isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return chain.handle(request);
    }
}
