package pl.training.workshop.m3.s07_srp.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m3.s07_srp.Sale;

/**
 * Krok 1: Extract Method według aktora - sekcja księgowości i sekcja marketingu.
 * Komentarze "// księgowość" i "// marketing" stały się nazwami metod.
 * Wspólny helper {@code revenue} nadal łączy obu aktorów.
 */
public final class DailyReport {
    public String render(List<Sale> sales) {
        return accountingSection(sales) + marketingSection(sales);
    }

    private String accountingSection(List<Sale> sales) {
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

    private String marketingSection(List<Sale> sales) {
        StringBuilder out = new StringBuilder();
        out.append("MARKETING\n");
        Map<String, BigDecimal> byTitle = new TreeMap<>();
        int sold = 0;
        for (Sale sale : sales) {
            byTitle.merge(sale.title(), revenue(sale), BigDecimal::add);
            sold += sale.tickets();
        }
        String hit = "brak";
        BigDecimal best = BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : byTitle.entrySet()) {
            if (entry.getValue().compareTo(best) > 0) {
                hit = entry.getKey() + " (" + entry.getValue() + ")";
                best = entry.getValue();
            }
        }
        out.append("Hit dnia: ").append(hit).append('\n');
        out.append("Sprzedanych biletow: ").append(sold).append('\n');
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
