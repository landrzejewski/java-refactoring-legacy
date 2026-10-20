package pl.training.workshop.m5.s28_chain.start;

import java.time.Duration;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;
import pl.training.workshop.shared.Money;

/**
 * Start: kto zatwierdza zwrot, rozstrzyga łańcuch if/else. Kolejność warunków jest regułą
 * biznesową (np. zwrot automatyczny nie ma limitu kwoty, bo stoi przed kasjerem), ale nie ma
 * nazwy - zmiana kolejności, dodanie poziomu albo innej ścieżki dla festiwalu oznacza edycję
 * tej jednej metody. Gdy nikt nie jest uprawniony, prośba "spada" z końca łańcucha.
 */
public final class RefundApprover {
    private static final Money CASHIER_LIMIT = Money.of("100.00");
    private static final Money MANAGER_LIMIT = Money.of("1000.00");

    public RefundDecision decide(RefundRequest request) {
        if (!request.amount().isGreaterThan(Money.ZERO)) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (!request.requestedAt().isBefore(request.screeningStart())) {
            return RefundDecision.rejected("seans juz sie rozpoczal");
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
