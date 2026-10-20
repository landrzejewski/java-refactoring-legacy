package pl.training.workshop.m5.s28_chain.step3;

import java.time.Duration;
import java.util.Objects;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 3: co najmniej 24 h przed seansem zatwierdza (bez limitu kwoty) i kończy łańcuch. */
public final class AutoRefundHandler implements RefundHandler {
    private final RefundHandler next;

    public AutoRefundHandler(RefundHandler next) {
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public RefundDecision handle(RefundRequest request) {
        if (Duration.between(request.requestedAt(), request.screeningStart()).toHours() >= 24) {
            return RefundDecision.approvedBy("AUTO");
        }
        return next.handle(request);
    }
}
