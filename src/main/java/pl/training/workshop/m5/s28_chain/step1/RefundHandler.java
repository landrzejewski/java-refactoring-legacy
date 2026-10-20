package pl.training.workshop.m5.s28_chain.step1;

import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 1: ogniwo decyzji - pusta odpowiedź znaczy "to nie moja sprawa, pytaj dalej". */
@FunctionalInterface
public interface RefundHandler {
    Optional<RefundDecision> handle(RefundRequest request);
}
