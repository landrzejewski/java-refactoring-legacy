package pl.training.module1;

import java.math.BigDecimal;

public final class SalesCalculations {
    private SalesCalculations() {
    }

    public static BigDecimal invoiceLineTotal(
            BigDecimal unitPrice,
            int quantity,
            boolean vip) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (vip) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total;
    }

    public static BigDecimal quoteLineTotal(
            BigDecimal unitPrice,
            int quantity,
            boolean vip) {
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        if (vip) {
            total = total.multiply(new BigDecimal("0.90"));
        }

        return total;
    }
}
