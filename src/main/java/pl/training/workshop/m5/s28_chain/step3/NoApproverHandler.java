package pl.training.workshop.m5.s28_chain.step3;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 3: jawny koniec łańcucha - żądanie nie może po cichu "spaść" bez odpowiedzi. */
public final class NoApproverHandler implements RefundHandler {
    @Override
    public RefundDecision handle(RefundRequest request) {
        return RefundDecision.rejected("brak uprawnionego zatwierdzajacego");
    }
}
