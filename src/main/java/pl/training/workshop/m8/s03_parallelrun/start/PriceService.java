package pl.training.workshop.m8.s03_parallelrun.start;

import pl.training.workshop.m8.s03_parallelrun.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Start: usługa korzysta tylko ze starego kalkulatora. Kandydat leży obok nieużywany -
 * jedyne opcje to "włączyć i zobaczyć" albo wieczne testy ręczne.
 */
public final class PriceService {
    private final LegacyPriceCalculator legacy = new LegacyPriceCalculator();

    public Money price(TicketQuery query) {
        return legacy.price(query);
    }
}
