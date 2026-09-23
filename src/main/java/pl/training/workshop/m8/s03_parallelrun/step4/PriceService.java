package pl.training.workshop.m8.s03_parallelrun.step4;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 4: przełączenie. Wybór ścieżki w jednym miejscu; CANDIDATE czyni nowy kalkulator
 * autorytatywnym, a LEGACY pozostaje natychmiastowym wycofaniem aż do usunięcia starego kodu.
 */
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
    private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
    private final MigrationMode mode;
    private final VerificationReport report;

    public PriceService() {
        this(MigrationMode.SHADOW, new VerificationReport());
    }

    public PriceService(MigrationMode mode, VerificationReport report) {
        this.mode = mode;
        this.report = report;
    }

    public Money price(TicketQuery query) {
        return switch (mode) {
            case LEGACY -> legacy.price(query);
            case SHADOW -> shadow(query);
            case CANDIDATE -> candidate.price(query);
        };
    }

    private Money shadow(TicketQuery query) {
        Money result = legacy.price(query);
        try {
            Money candidatePrice = candidate.price(query);
            report.record(result.equals(candidatePrice)
                    ? new VerificationReport.Agreement(query, result)
                    : new VerificationReport.Divergence(query, result, candidatePrice));
        } catch (RuntimeException failure) {
            report.record(new VerificationReport.CandidateFailure(query, result,
                    failure.getClass().getSimpleName() + ": " + failure.getMessage()));
        }
        return result;
    }
}
