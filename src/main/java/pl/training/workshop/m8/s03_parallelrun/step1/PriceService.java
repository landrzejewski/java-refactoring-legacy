package pl.training.workshop.m8.s03_parallelrun.step1;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: dodanie cienia. Kandydat liczy na tym samym wejściu, ale klient zawsze dostaje
 * wynik legacy. Awaria kandydata jest izolowana (try/catch), a nie przerywa sprzedaży.
 */
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();
    private final CandidatePriceCalculator candidate = new CandidatePriceCalculator();
    private int mismatches;

    public Money price(TicketQuery query) {
        Money result = legacy.price(query);
        try {
            if (!candidate.price(query).equals(result)) {
                mismatches++;
            }
        } catch (RuntimeException candidateFailure) {
            mismatches++;
        }
        return result;
    }

    /** Wiemy, ŻE coś się różni, ale nie wiemy CO - licznik to za mało do decyzji. */
    public int mismatches() {
        return mismatches;
    }
}
