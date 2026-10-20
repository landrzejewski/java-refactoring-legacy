package pl.training.workshop.m5.s28_chain.step2;

import java.util.List;
import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: każda gałąź to ogniwo, a łańcuch if zastąpiła lista przeglądana po kolei.
 * Kolejność reguł jest już nazwana i widoczna w jednym miejscu. Koniec listy bez decyzji
 * to nadal jawny przypadek "brak uprawnionego zatwierdzającego".
 */
public final class RefundApprover {
    private static final Money CASHIER_LIMIT = Money.of("100.00");
    private static final Money MANAGER_LIMIT = Money.of("1000.00");

    private final List<RefundHandler> handlers = List.of(
            new AfterScreeningHandler(),
            new AutoRefundHandler(),
            new CashierHandler(CASHIER_LIMIT),
            new ManagerHandler(MANAGER_LIMIT));

    public RefundDecision decide(RefundRequest request) {
        if (!request.amount().isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        for (RefundHandler handler : handlers) {
            Optional<RefundDecision> decision = handler.handle(request);
            if (decision.isPresent()) {
                return decision.get();
            }
        }
        return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
    }
}
