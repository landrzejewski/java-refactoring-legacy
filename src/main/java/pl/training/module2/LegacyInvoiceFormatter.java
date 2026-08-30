package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public final class LegacyInvoiceFormatter {
    public String format(String customer, List<InvoiceLine> lines) {
        BigDecimal subtotal = BigDecimal.ZERO;
        StringBuilder result = new StringBuilder("INVOICE\n");

        String displayedCustomer = customer == null
                ? "UNKNOWN"
                : customer.trim().toUpperCase(Locale.ROOT);
        result.append("Customer: ").append(displayedCustomer).append('\n');

        for (InvoiceLine line : lines) {
            BigDecimal lineTotal = line.unitPrice()
                    .multiply(BigDecimal.valueOf(line.quantity()));
            subtotal = subtotal.add(lineTotal);

            result.append(line.sku())
                    .append(" x ")
                    .append(line.quantity())
                    .append(" = ")
                    .append(lineTotal.setScale(2, RoundingMode.HALF_UP))
                    .append('\n');
        }

        BigDecimal tax = subtotal
                .multiply(new BigDecimal("0.23"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        result.append("Subtotal: ")
                .append(subtotal.setScale(2, RoundingMode.HALF_UP))
                .append('\n');
        result.append("Tax: ").append(tax).append('\n');
        result.append("Total: ").append(total).append('\n');

        return result.toString();
    }
}
