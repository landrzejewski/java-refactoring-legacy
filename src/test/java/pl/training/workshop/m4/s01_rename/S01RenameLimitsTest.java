package pl.training.workshop.m4.s01_rename;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

/** Granice automatycznego Rename: nazwy, których IDE nie widzi jako użyć symbolu. */
final class S01RenameLimitsTest {
    @Test
    void reflectionTurnsJavaNamesIntoTheCsvHeader() {
        assertEquals("t;n;d", header(pl.training.workshop.m4.s01_rename.start.SalesReport.Line.class));
        assertEquals("title;tickets;revenue",
                header(pl.training.workshop.m4.s01_rename.step3.SalesReport.Line.class),
                "po Rename refleksja dałaby nowy nagłówek - dlatego step3 ma jawny CSV_HEADER");
    }

    @Test
    void configurationStillNamesTheOldMethod() throws ReflectiveOperationException {
        Class<?> report = pl.training.workshop.m4.s01_rename.step3.SalesReport.class;
        report.getMethod("calc2", List.class, boolean.class);
        report.getMethod("revenueCsv", List.class, boolean.class);
        assertThrows(NoSuchMethodException.class,
                () -> report.getMethod("revenue", List.class, boolean.class),
                "refleksja szuka nazwy z tekstu; bez delegatu calc2 zadanie przestaje działać");
    }

    private static String header(Class<? extends Record> line) {
        return Arrays.stream(line.getRecordComponents())
                .map(RecordComponent::getName)
                .collect(Collectors.joining(";"));
    }
}
