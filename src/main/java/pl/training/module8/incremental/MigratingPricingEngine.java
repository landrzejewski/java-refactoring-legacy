package pl.training.module8.incremental;

import java.util.Objects;

public final class MigratingPricingEngine implements PricingEngine {
    private final PricingEngine legacy;
    private final PricingEngine candidate;
    private final VerificationReporter reporter;
    private final MigrationMode mode;

    public MigratingPricingEngine(
            PricingEngine legacy,
            PricingEngine candidate,
            VerificationReporter reporter,
            MigrationMode mode) {
        this.legacy = Objects.requireNonNull(legacy, "legacy");
        this.candidate = Objects.requireNonNull(candidate, "candidate");
        this.reporter = Objects.requireNonNull(reporter, "reporter");
        this.mode = Objects.requireNonNull(mode, "mode");
    }

    @Override
    public PriceQuote quote(PriceRequest request) {
        Objects.requireNonNull(request, "request");
        return switch (mode) {
            case LEGACY -> requireQuote(legacy.quote(request), "legacy quote");
            case VERIFY -> verify(request);
            case CANDIDATE -> requireQuote(
                    candidate.quote(request), "candidate quote");
        };
    }

    private PriceQuote verify(PriceRequest request) {
        PriceQuote legacyQuote = requireQuote(
                legacy.quote(request), "legacy quote");
        try {
            PriceQuote candidateQuote = requireQuote(
                    candidate.quote(request), "candidate quote");
            VerificationEvent event = legacyQuote.equals(candidateQuote)
                    ? new VerificationEvent.Agreement(request, legacyQuote)
                    : new VerificationEvent.Divergence(
                            request, legacyQuote, candidateQuote);
            tryToReport(event);
        } catch (RuntimeException failure) {
            tryToReport(
                    new VerificationEvent.CandidateFailure(
                            request,
                            legacyQuote,
                            failure.getClass().getName(),
                            Objects.toString(failure.getMessage(), "")));
        }
        return legacyQuote;
    }

    private void tryToReport(VerificationEvent event) {
        try {
            reporter.report(event);
        } catch (RuntimeException ignored) {
            // Awaria reportera nie może zastąpić wyniku legacy.
        }
    }

    private static PriceQuote requireQuote(
            PriceQuote quote,
            String message) {
        return Objects.requireNonNull(quote, message);
    }
}
