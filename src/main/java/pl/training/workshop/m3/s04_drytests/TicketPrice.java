package pl.training.workshop.m3.s04_drytests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

/** Kod produkcyjny sceny (stabilny): cena biletu według taryfy, minus 5.00 za seans poranny. */
public final class TicketPrice {
    private final Tariff tariff;

    public TicketPrice(Tariff tariff) {
        this.tariff = tariff;
    }

    public Tariff tariff() {
        return tariff;
    }

    public BigDecimal of(String format, String type, LocalTime start) {
        BigDecimal base = tariff.basePrices().get(format);
        BigDecimal discount = base.multiply(BigDecimal.valueOf(tariff.discountPercents().get(type)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal price = base.subtract(discount);
        if (start.getHour() < 12) {
            price = price.subtract(new BigDecimal("5.00"));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
