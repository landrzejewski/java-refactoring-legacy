package pl.training.workshop.m6.s20_decisionmap.step3;

import java.time.DayOfWeek;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (alternatywa dla kroku 2, budowana od kroku 1): Replace Type Code with Class -
 * wiedza o cenie należy do formatu. Reguła dnia zostaje zwykłym switchem, bo jest stabilna.
 */
public final class ShowPricing {
    public Money price(DayOfWeek day, String format) {
        return Format.of(format).priceOn(day);
    }
}
