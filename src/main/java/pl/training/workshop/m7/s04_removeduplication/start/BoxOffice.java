package pl.training.workshop.m7.s04_removeduplication.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Start: kasa liczy rabat grupowy (10+ biletów = -10%). Ta sama reguła żyje też w WebShop -
 * inaczej zapisana i z innym zaokrągleniem. To duplikacja wiedzy, nie tylko tekstu.
 */
public final class BoxOffice {
    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal price : ticketPrices) {
            sum = sum.add(price);
        }
        if (ticketPrices.size() > 9) {
            BigDecimal discount = sum.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            sum = sum.subtract(discount);
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }
}
