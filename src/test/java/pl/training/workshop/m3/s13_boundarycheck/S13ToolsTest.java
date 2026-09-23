package pl.training.workshop.m3.s13_boundarycheck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Testy samych narzędzi na stałych próbkach kodu (niezależnie od edycji start na żywo). */
final class S13ToolsTest {
    @TempDir
    Path dir;

    @Test
    void probeFindsTwoConceptsInOneClass() throws IOException {
        Path file = Files.writeString(dir.resolve("Mixed.java"), """
                public final class Mixed {
                    private final int price;
                    private final String table;

                    public Mixed(int price, String table) {
                        this.price = price;
                        this.table = table;
                    }

                    public int total(int seats) {
                        return price * seats + fee();
                    }

                    private int fee() {
                        return 2;
                    }

                    public String row(String title) {
                        return table + ";" + title;
                    }
                }
                """);
        assertEquals(new CohesionProbe.Result(2, List.of("fee, total", "row")), new CohesionProbe().analyze(file));
    }

    @Test
    void boundaryRuleReportsForbiddenImportsIncludingStatic() throws IOException {
        Files.writeString(dir.resolve("Policy.java"), """
                package app.domain;

                import static app.adapter.Db.connect;
                import java.sql.Connection;
                import java.util.List;
                """);
        assertEquals(List.of("Policy.java: app.adapter.Db.connect", "Policy.java: java.sql.Connection"),
                new BoundaryRule("java.sql.", ".adapter.").violations(dir));
    }
}
