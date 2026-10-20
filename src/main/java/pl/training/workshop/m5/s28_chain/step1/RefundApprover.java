package pl.training.workshop.m5.s28_chain.step1;

import java.time.Duration;
import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Interface + Extract Class dla pierwszej gałęzi. Reszta łańcucha if bez zmian;
 * walidacja kwoty zostaje w approverze - to warunek wstępny, nie poziom zatwierdzania.
 */
public final class RefundApprover {
    private static final Money CASHIER_LIMIT = Money.of("100.00");
    private static final Money MANAGER_LIMIT = Money.of("1000.00");

    private final RefundHandler afterScreening = new AfterScreeningHandler();

    public RefundDecision decide(RefundRequest request) {
        if (!request.amount().isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        Optional<RefundDecision> decision = afterScreening.handle(request);
        if (decision.isPresent()) {
            return decision.get();
        } else if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
            return RefundDecision.approvedBy("AUTO");
        } else if (!request.amount().isGreaterThan(CASHIER_LIMIT)) {
            return RefundDecision.approvedBy("CASHIER");
        } else if (!request.amount().isGreaterThan(MANAGER_LIMIT)) {
            return RefundDecision.approvedBy("MANAGER");
        }
        return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
    }
}
