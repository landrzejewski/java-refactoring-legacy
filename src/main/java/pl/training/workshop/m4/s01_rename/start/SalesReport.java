package pl.training.workshop.m4.s01_rename.start;

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
 * Start: raport sprzedaży dla dystrybutora (CSV). Nazwy nic nie mówią: calc2, s, flag, m, x, l,
 * a komponenty wiersza to t, n, d. Dwie nazwy żyją też POZA Javą:
 * "calc2" w konfiguracji ReportJob (refleksja) i t/n/d w nagłówku CSV (refleksja po rekordzie).
 */
public final class SalesReport {
    /** Wiersz raportu. Nazwy komponentów trafiają do nagłówka CSV. */
    public record Line(String t, int n, BigDecimal d) {
    }

    /** Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji. */
    public String calc2(List<Sale> s, boolean flag) {
        Map<String, Line> m = new TreeMap<>();
        for (Sale x : s) {
            if (flag && !x.online()) {
                continue;
            }
            Line l = m.get(x.title());
            if (l == null) {
                m.put(x.title(), new Line(x.title(), x.tickets(), x.amount()));
            } else {
                m.put(x.title(), new Line(x.title(), l.n() + x.tickets(), l.d().add(x.amount())));
            }
        }
        StringBuilder b = new StringBuilder(header()).append('\n');
        for (Line l : m.values()) {
            b.append(row(l)).append('\n');
        }
        return b.toString();
    }

    private static String header() {
        return Arrays.stream(Line.class.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.joining(";"));
    }

    private static String row(Line l) {
        List<String> v = new ArrayList<>();
        for (RecordComponent c : Line.class.getRecordComponents()) {
            try {
                v.add(String.valueOf(c.getAccessor().invoke(l)));
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }
        return String.join(";", v);
    }
}
