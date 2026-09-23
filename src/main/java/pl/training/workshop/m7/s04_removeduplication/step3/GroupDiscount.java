package pl.training.workshop.m7.s04_removeduplication.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Krok 3: Extract Class - reguła "10+ biletów = -10%, rabat zaokrąglany HALF_UP" ma
 * jednego właściciela. Opłata rezerwacyjna nie jest częścią reguły i zostaje w WebShop.
 */
final class GroupDiscount {
    private static final int GROUP_SIZE = 10;
    private static final BigDecimal GROUP_DISCOUNT = new BigDecimal("0.10");

    private GroupDiscount() {
    }

    static BigDecimal ticketsTotal(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = BigDecimal.ZERO;
        for (BigDecimal price : ticketPrices) {
            tickets = tickets.add(price);
        }
        if (ticketPrices.size() >= GROUP_SIZE) {
            BigDecimal discount = tickets.multiply(GROUP_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
            tickets = tickets.subtract(discount);
        }
        return tickets;
    }
}
