package pl.training.workshop.m8.s03_parallelrun.start;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Start: nowy kalkulator (Money) napisany obok starego. Nikt jeszcze nie sprawdził,
 * czy na ruchu produkcyjnym liczy tak samo - dlatego nie wolno go po prostu włączyć.
 */
public final class CandidatePriceCalculator {
    private static final Money MORNING_DISCOUNT = Money.of("5.00");
    private static final Money VIP_SURCHARGE = Money.of("10.00");
    private static final Money GLASSES_3D = Money.of("3.00");

    public Money price(TicketQuery query) {
        Money base = basePrice(query.format());
        if (query.start().getHour() < 12) {
            base = base.minus(MORNING_DISCOUNT);
        }
        Money price = base.minus(base.percent(discountPercent(query.type())));
        if (query.row() >= 10) {
            price = price.plus(VIP_SURCHARGE);
        }
        if (query.format().equals("3D")) {
            price = price.plus(GLASSES_3D);
        }
        return price;
    }

    private static Money basePrice(String format) {
        return switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("Nieznany format: " + format);
        };
    }

    private static int discountPercent(String type) {
        return switch (type) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> 0;
        };
    }
}
