package pl.training.workshop.m7.s04_removeduplication.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Krok 1: ujednolicenie zapisu - stałe GROUP_SIZE i GROUP_DISCOUNT, warunek >= zamiast > 9,
 * zmienna tickets. Zachowanie bez zmian; różnica względem WebShop to już tylko tryb zaokrąglenia.
 */
public final class BoxOffice {
    private static final int GROUP_SIZE = 10;
    private static final BigDecimal GROUP_DISCOUNT = new BigDecimal("0.10");

    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = BigDecimal.ZERO;
        for (BigDecimal price : ticketPrices) {
            tickets = tickets.add(price);
        }
        if (ticketPrices.size() >= GROUP_SIZE) {
            BigDecimal discount = tickets.multiply(GROUP_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
            tickets = tickets.subtract(discount);
        }
        return tickets.setScale(2, RoundingMode.HALF_UP);
    }
}
