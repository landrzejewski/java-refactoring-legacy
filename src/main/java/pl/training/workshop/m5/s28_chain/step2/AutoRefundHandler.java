package pl.training.workshop.m5.s28_chain.step2;

import java.time.Duration;
import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 2: co najmniej 24 h przed seansem zwrot jest automatyczny - bez limitu kwoty. */
public final class AutoRefundHandler implements RefundHandler {
    @Override
    public Optional<RefundDecision> handle(RefundRequest request) {
        if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
            return Optional.of(RefundDecision.approvedBy("AUTO"));
        }
        return Optional.empty();
    }
}
