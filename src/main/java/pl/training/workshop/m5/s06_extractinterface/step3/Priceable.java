package pl.training.workshop.m5.s06_extractinterface.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): metoda domyślna {@code vatAmount()} dodana do opublikowanego interfejsu
 * bez łamania implementacji. Używa wyłącznie operacji kontraktu, więc jest poprawna dla każdej z nich.
 * (Nowa metoda ABSTRAKCYJNA złamałaby każdą istniejącą implementację - źródłowo i binarnie.)
 */
public interface Priceable {
    Money price();

    int vatPercent();

    /** Kwota VAT zawarta w cenie brutto: brutto * stawka / (100 + stawka). */
    default Money vatAmount() {
        BigDecimal rate = BigDecimal.valueOf(vatPercent());
        return new Money(price().amount().multiply(rate)
                .divide(rate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP));
    }
}
