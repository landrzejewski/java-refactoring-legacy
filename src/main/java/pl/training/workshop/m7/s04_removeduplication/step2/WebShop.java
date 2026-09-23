package pl.training.workshop.m7.s04_removeduplication.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Krok 2: świadoma decyzja (zmiana kontraktu, osobny commit) - biznes potwierdził,
 * że rabat zaokrąglamy HALF_UP jak w kasie. Dla rabatu z końcówką 5 na trzecim miejscu
 * po przecinku wynik online się zmienia.
 */
public final class WebShop {
    private static final int GROUP_SIZE = 10;
    private static final BigDecimal GROUP_DISCOUNT = new BigDecimal("0.10");
    private static final BigDecimal FEE = new BigDecimal("2.00");

    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = BigDecimal.ZERO;
        for (BigDecimal price : ticketPrices) {
            tickets = tickets.add(price);
        }
        if (ticketPrices.size() >= GROUP_SIZE) {
            BigDecimal discount = tickets.multiply(GROUP_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
            tickets = tickets.subtract(discount);
        }
        BigDecimal fees = FEE.multiply(BigDecimal.valueOf(ticketPrices.size()));
        return tickets.add(fees).setScale(2, RoundingMode.HALF_UP);
    }
}
