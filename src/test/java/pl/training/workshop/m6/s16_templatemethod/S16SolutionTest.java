package pl.training.workshop.m6.s16_templatemethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m6.s16_templatemethod.step2.SalesReport;
import pl.training.workshop.shared.Money;

/** Szkielet jest chroniony (final), a nowy format to tylko trzy metody. */
final class S16SolutionTest {
    @Test
    void templateMethodIsFinal() throws NoSuchMethodException {
        assertTrue(Modifier.isFinal(SalesReport.class.getMethod("render", List.class).getModifiers()));
    }

    @Test
    void newFormatReusesTheSkeleton() {
        SalesReport markdown = new SalesReport() {
            @Override
            protected String header() {
                return "| godzina | film |\n";
            }

            @Override
            protected String row(Sale sale) {
                return "| " + sale.time() + " | " + sale.title() + " |\n";
            }

            @Override
            protected String footer(int tickets, Money total) {
                return "Razem: " + total + "\n";
            }
        };
        assertEquals("| godzina | film |\n| 18:00 | Diuna |\nRazem: 40.00\n",
                markdown.render(List.of(new Sale(LocalTime.of(18, 0), "Diuna", 1, Money.of("40.00")))));
    }
}
