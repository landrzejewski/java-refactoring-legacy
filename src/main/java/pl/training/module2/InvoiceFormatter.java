package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public final class InvoiceFormatter {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.23");

    public String format(String customer, List<InvoiceLine> lines) {
        BigDecimal subtotal = calculateSubtotal(lines);
        BigDecimal tax = money(subtotal.multiply(TAX_RATE));
        BigDecimal total = money(subtotal.add(tax));

        StringBuilder result = new StringBuilder("INVOICE\n")
                .append("Customer: ")
                .append(displayedCustomer(customer))
                .append('\n');

        appendLines(result, lines);

        return result
                .append("Subtotal: ").append(money(subtotal)).append('\n')
                .append("Tax: ").append(tax).append('\n')
                .append("Total: ").append(total).append('\n')
                .toString();
    }

    private static String displayedCustomer(String customer) {
        return customer == null
                ? "UNKNOWN"
                : customer.trim().toUpperCase(Locale.ROOT);
    }

    private static BigDecimal calculateSubtotal(List<InvoiceLine> lines) {
        return lines.stream()
                .map(InvoiceFormatter::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void appendLines(
            StringBuilder result,
            List<InvoiceLine> lines) {
        for (InvoiceLine line : lines) {
            result.append(line.sku())
                    .append(" x ")
                    .append(line.quantity())
                    .append(" = ")
                    .append(money(lineTotal(line)))
                    .append('\n');
        }
    }

    private static BigDecimal lineTotal(InvoiceLine line) {
        return line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
