package pl.training.workshop.m5.s28_chain.step2;

import java.util.Optional;

import pl.training.workshop.m5.s28_chain.RefundDecision;
import pl.training.workshop.m5.s28_chain.RefundRequest;

/** Krok 2: bez zmian - ogniwo decyzji - pusta odpowiedź znaczy "to nie moja sprawa, pytaj dalej". */
@FunctionalInterface
public interface RefundHandler {
    Optional<RefundDecision> handle(RefundRequest request);
}
