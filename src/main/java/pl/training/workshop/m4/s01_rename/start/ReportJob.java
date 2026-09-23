package pl.training.workshop.m4.s01_rename.start;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import pl.training.workshop.m4.s01_rename.Sale;

/**
 * Nocne zadanie raportu. Metodę wybiera konfiguracja, a nie kod - IDE nie widzi tego użycia.
 * W produkcji CONFIG to plik report.properties na serwerze, poza repozytorium.
 */
public final class ReportJob {
    static final String CONFIG = """
            report.method=calc2
            report.onlineOnly=true
            """;

    private final SalesReport report = new SalesReport();

    public String run(List<Sale> sales) {
        Properties config = new Properties();
        try {
            config.load(new StringReader(CONFIG));
            Method method = SalesReport.class.getMethod(
                    config.getProperty("report.method"), List.class, boolean.class);
            boolean onlineOnly = Boolean.parseBoolean(config.getProperty("report.onlineOnly"));
            return (String) method.invoke(report, sales, onlineOnly);
        } catch (IOException | ReflectiveOperationException e) {
            throw new IllegalStateException("Zadanie raportu nie działa: " + e, e);
        }
    }
}
