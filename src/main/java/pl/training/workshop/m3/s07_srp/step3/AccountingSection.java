package pl.training.workshop.m3.s07_srp.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.training.workshop.m3.s07_srp.Sale;

/** Krok 3: aktor - księgowość. Zmienia się, gdy zmieniają się stawki VAT lub wymogi sprawozdań. */
final class AccountingSection {
    String render(List<Sale> sales) {
        StringBuilder out = new StringBuilder();
        BigDecimal tickets = new BigDecimal("0.00");
        BigDecimal bar = new BigDecimal("0.00");
        for (Sale sale : sales) {
            tickets = tickets.add(sale.ticketRevenue());
            bar = bar.add(sale.barRevenue());
        }
        out.append("KSIEGOWOSC\n");
        out.append("Bilety brutto ").append(tickets)
                .append(", netto ").append(net(tickets, 8)).append('\n');
        out.append("Bar brutto ").append(bar).append(", netto ").append(net(bar, 23)).append('\n');
        BigDecimal total = new BigDecimal("0.00");
        for (Sale sale : sales) {
            total = total.add(revenue(sale));
        }
        out.append("Razem brutto ").append(total).append('\n');
        return out.toString();
    }

    private BigDecimal revenue(Sale sale) {
        return sale.ticketRevenue().add(sale.barRevenue());
    }

    private BigDecimal net(BigDecimal gross, int vatPercent) {
        return gross.multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(100 + vatPercent), 2, RoundingMode.HALF_UP);
    }
}
