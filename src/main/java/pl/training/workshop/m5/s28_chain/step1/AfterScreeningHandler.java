package pl.training.workshop.m5.s28_chain.step1;

import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 1: pierwsza gałąź jako ogniwo - po rozpoczęciu seansu zwrotu nie ma. */
public final class AfterScreeningHandler implements RefundHandler {
    @Override
    public Optional<RefundDecision> handle(RefundRequest request) {
        if (!request.requestedAt().isBefore(request.screeningStart())) {
            return Optional.of(RefundDecision.rejected("seans juz sie rozpoczal"));
        }
        return Optional.empty();
    }
}
