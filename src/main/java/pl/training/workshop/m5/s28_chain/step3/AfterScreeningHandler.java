package pl.training.workshop.m5.s28_chain.step3;

import java.util.Objects;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 3: po rozpoczęciu seansu odrzuca i kończy łańcuch; wcześniej przekazuje dalej. */
public final class AfterScreeningHandler implements RefundHandler {
    private final RefundHandler next;

    public AfterScreeningHandler(RefundHandler next) {
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public RefundDecision handle(RefundRequest request) {
        if (!request.requestedAt().isBefore(request.screeningStart())) {
            return RefundDecision.rejected("seans juz sie rozpoczal");
        }
        return next.handle(request);
    }
}
