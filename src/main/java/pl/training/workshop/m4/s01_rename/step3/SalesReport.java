package pl.training.workshop.m4.s01_rename.step3;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.training.workshop.m4.s01_rename.Sale;

/**
 * Krok 3: Rename komponentów wiersza t, n, d -> title, tickets, revenue.
 * Nagłówek CSV był wyliczany refleksją z nazw Javy, więc sam Rename zmieniłby plik
 * dla dystrybutora. Dlatego odcinamy kontrakt zewnętrzny od nazw w kodzie:
 * jawny nagłówek i jawny wiersz.
 */
public final class SalesReport {
    /** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w Javie. */
    static final String CSV_HEADER = "t;n;d";

    public record Line(String title, int tickets, BigDecimal revenue) {
    }

    /**
     * Stara nazwa z konfiguracji report.properties (report.method=calc2).
     * Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
     */
    @Deprecated
    public String calc2(List<Sale> sales, boolean onlineOnly) {
        return revenueCsv(sales, onlineOnly);
    }

    public String revenueCsv(List<Sale> sales, boolean onlineOnly) {
        Map<String, Line> linesByTitle = new TreeMap<>();
        for (Sale sale : sales) {
            if (onlineOnly && !sale.online()) {
                continue;
            }
            Line previous = linesByTitle.get(sale.title());
            if (previous == null) {
                linesByTitle.put(sale.title(), new Line(sale.title(), sale.tickets(), sale.amount()));
            } else {
                linesByTitle.put(sale.title(), new Line(sale.title(),
                        previous.tickets() + sale.tickets(), previous.revenue().add(sale.amount())));
            }
        }
        StringBuilder csv = new StringBuilder(CSV_HEADER).append('\n');
        for (Line line : linesByTitle.values()) {
            csv.append(row(line)).append('\n');
        }
        return csv.toString();
    }

    private static String row(Line line) {
        return line.title() + ";" + line.tickets() + ";" + line.revenue();
    }
}
