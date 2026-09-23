package pl.training.workshop.m4.s01_rename.step1;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pl.training.workshop.m4.s01_rename.Sale;

/**
 * Krok 1: Rename zmiennych lokalnych i parametrów (s, flag, m, x, l, b, v, c).
 * Zasięg lokalny, brak użyć poza metodą - IDE robi to w pełni bezpiecznie.
 */
public final class SalesReport {
    /** Wiersz raportu. Nazwy komponentów trafiają do nagłówka CSV. */
    public record Line(String t, int n, BigDecimal d) {
    }

    /** Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji. */
    public String calc2(List<Sale> sales, boolean onlineOnly) {
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
                        previous.n() + sale.tickets(), previous.d().add(sale.amount())));
            }
        }
        StringBuilder csv = new StringBuilder(header()).append('\n');
        for (Line line : linesByTitle.values()) {
            csv.append(row(line)).append('\n');
        }
        return csv.toString();
    }

    private static String header() {
        return Arrays.stream(Line.class.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.joining(";"));
    }

    private static String row(Line line) {
        List<String> values = new ArrayList<>();
        for (RecordComponent component : Line.class.getRecordComponents()) {
            try {
                values.add(String.valueOf(component.getAccessor().invoke(line)));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
        return String.join(";", values);
    }
}
