package pl.training.workshop.m8.s03_parallelrun.step3;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 3 (bez zmian): tryb shadow z raportem. Po poprawce kandydata raport nie ma rozbieżności,
 * zostaje tylko świadomie zaakceptowana różnica dla nieznanego formatu.
 */
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
    private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
    private final VerificationReport report;

    public PriceService() {
        this(new VerificationReport());
    }

    public PriceService(VerificationReport report) {
        this.report = report;
    }

    public Money price(TicketQuery query) {
        Money result = legacy.price(query);
        report.record(verify(query, result));
        return result;
    }

    private VerificationReport.Verification verify(TicketQuery query, Money legacyPrice) {
        try {
            Money candidatePrice = candidate.price(query);
            return legacyPrice.equals(candidatePrice)
                    ? new VerificationReport.Agreement(query, legacyPrice)
                    : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
        } catch (RuntimeException failure) {
            return new VerificationReport.CandidateFailure(query, legacyPrice,
                    failure.getClass().getSimpleName() + ": " + failure.getMessage());
        }
    }
}
