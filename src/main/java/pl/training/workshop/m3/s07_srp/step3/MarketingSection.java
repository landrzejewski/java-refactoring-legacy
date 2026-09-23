package pl.training.workshop.m3.s07_srp.step3;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m3.s07_srp.Sale;

/** Krok 3: aktor - marketing. Zmienia się, gdy marketing zmienia definicję "hitu". */
final class MarketingSection {
    String render(List<Sale> sales) {
        StringBuilder out = new StringBuilder();
        out.append("MARKETING\n");
        Map<String, BigDecimal> byTitle = new TreeMap<>();
        int sold = 0;
        for (Sale sale : sales) {
            byTitle.merge(sale.title(), popularity(sale), BigDecimal::add);
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

    private BigDecimal popularity(Sale sale) {
        return sale.ticketRevenue().add(sale.barRevenue());
    }
}
