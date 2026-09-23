package pl.training.workshop.m8.s03_parallelrun.step3;

import java.math.BigDecimal;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/** Krok 3 (bez zmian): stary kalkulator - w trybie SHADOW jego wynik jest autorytatywny. */
public final class LegacyPriceCalculator {
    public Money price(TicketQuery q) {
        double p = 0;
        if (q.format().equals("2D")) {
            p = 25.00;
        } else if (q.format().equals("3D")) {
            p = 32.00;
        } else if (q.format().equals("IMAX")) {
            p = 40.00;
        }
        if (q.type().equals("STUDENT")) {
            p = p - p * 0.25;
        } else if (q.type().equals("SENIOR")) {
            p = p - p * 0.30;
        } else if (q.type().equals("CHILD")) {
            p = p - p * 0.40;
        }
        if (q.start().getHour() < 12) {
            p = p - 5;
        }
        if (q.row() >= 10) {
            p = p + 10;
        }
        if (q.format().equals("3D")) {
            p = p + 3;
        }
        return new Money(BigDecimal.valueOf(p));
    }
}
