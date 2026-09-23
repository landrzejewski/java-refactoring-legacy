package pl.training.workshop.m3.s04_drytests.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m3.s04_drytests.TicketPrice;

/**
 * Start: specyfikacja cen "maksymalnie DRY". Jeden helper, przypadki zaszyfrowane
 * w stringach ("3D S 11"), a oczekiwana cena liczona z tej samej taryfy i tym samym
 * wzorem co kod produkcyjny. Błąd w taryfie przechodzi niezauważony: test nie ma
 * niezależnej wyroczni. Zwraca listę niespełnionych przypadków (pusta = zielono).
 * <p>W projekcie byłaby to klasa testowa JUnit; w warsztacie leży w main,
 * żeby działał mechanizm start/stepN.
 */
public final class TicketPriceSpecs {
    private static final List<String> SPECS = List.of("IMAX N 20", "3D S 11", "2D E 18", "2D C 10");

    public List<String> run(TicketPrice price) {
        List<String> failures = new ArrayList<>();
        for (String spec : SPECS) {
            if (!check(price, spec)) {
                failures.add(spec);
            }
        }
        return failures;
    }

    private boolean check(TicketPrice price, String spec) {
        String[] p = spec.split(" ");
        String type = switch (p[1]) {
            case "S" -> "STUDENT";
            case "E" -> "SENIOR";
            case "C" -> "CHILD";
            default -> "NORMAL";
        };
        LocalTime start = LocalTime.of(Integer.parseInt(p[2]), 0);
        BigDecimal base = price.tariff().basePrices().get(p[0]);
        BigDecimal discount = base.multiply(BigDecimal.valueOf(price.tariff().discountPercents().get(type)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal expected = base.subtract(discount)
                .subtract(start.getHour() < 12 ? new BigDecimal("5.00") : BigDecimal.ZERO);
        return price.of(p[0], type, start).compareTo(expected) == 0;
    }
}
