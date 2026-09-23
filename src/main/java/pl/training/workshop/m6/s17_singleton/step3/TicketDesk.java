package pl.training.workshop.m6.s17_singleton.step3;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: jawne wstrzyknięcie. Konstruktor bezargumentowy zachowuje dotychczasowe zachowanie
 * (INSTANCE), a test lub inny cennik (np. promocyjny) nie wymaga globalnego stanu.
 */
public final class TicketDesk {
    private final Tariff tariff;

    public TicketDesk() {
        this(PriceList.INSTANCE);
    }

    public TicketDesk(Tariff tariff) {
        this.tariff = Objects.requireNonNull(tariff, "tariff");
    }

    public Money quote(String format, boolean online) {
        Money price = tariff.basePrice(format);
        return online ? price.plus(Money.of("2.00")) : price;
    }
}
