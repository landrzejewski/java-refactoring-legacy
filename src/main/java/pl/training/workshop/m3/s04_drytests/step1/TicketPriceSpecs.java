package pl.training.workshop.m3.s04_drytests.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m3.s04_drytests.Tariff;
import pl.training.workshop.m3.s04_drytests.TicketPrice;

/**
 * Krok 1: DAMP zamiast szyfru - każdy przypadek ma nazwę i jawne dane wejściowe,
 * a komunikat mówi, co się nie zgadza. Czytelniej, ale oczekiwanie nadal liczy
 * {@link #expectedFromTariff}, czyli kopia algorytmu produkcyjnego.
 */
public final class TicketPriceSpecs {
    public List<String> run(TicketPrice price) {
        List<String> failures = new ArrayList<>();
        check(price, "normalny na wieczornym IMAX", "IMAX", "NORMAL", LocalTime.of(20, 0), failures);
        check(price, "student na porannym 3D", "3D", "STUDENT", LocalTime.of(11, 0), failures);
        check(price, "senior na wieczornym 2D", "2D", "SENIOR", LocalTime.of(18, 0), failures);
        check(price, "dziecko na porannym 2D", "2D", "CHILD", LocalTime.of(10, 0), failures);
        return failures;
    }

    private void check(TicketPrice price, String example, String format, String type, LocalTime start,
                       List<String> failures) {
        BigDecimal expected = expectedFromTariff(price.tariff(), format, type, start);
        BigDecimal actual = price.of(format, type, start);
        if (actual.compareTo(expected) != 0) {
            failures.add(example + ": oczekiwano " + expected + ", jest " + actual);
        }
    }

    private BigDecimal expectedFromTariff(Tariff tariff, String format, String type, LocalTime start) {
        BigDecimal base = tariff.basePrices().get(format);
        BigDecimal discount = base.multiply(BigDecimal.valueOf(tariff.discountPercents().get(type)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal morning = start.getHour() < 12 ? new BigDecimal("5.00") : BigDecimal.ZERO;
        return base.subtract(discount).subtract(morning);
    }
}
