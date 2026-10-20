package pl.training.workshop.m5.s27_templatetostrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s27_templatetostrategy.step3.DailyPeriod;
import pl.training.workshop.m5.s27_templatetostrategy.step3.ReportFormat;
import pl.training.workshop.m5.s27_templatetostrategy.step3.ReportPeriod;
import pl.training.workshop.m5.s27_templatetostrategy.step3.ScreeningReport;
import pl.training.workshop.m5.s27_templatetostrategy.step3.WeeklyPeriod;
import pl.training.workshop.shared.Money;

/** Po kroku 3: jedna finalna klasa, osie dodają się zamiast mnożyć. */
final class S27SolutionTest {
    private static final String STEP3 = "pl.training.workshop.m5.s27_templatetostrategy.step3.";

    @Test
    void reportIsOneFinalConcreteClassComposedOfTwoStrategies() throws NoSuchMethodException {
        assertTrue(Modifier.isFinal(ScreeningReport.class.getModifiers()));
        assertFalse(Modifier.isAbstract(ScreeningReport.class.getModifiers()));
        ScreeningReport.class.getConstructor(ReportPeriod.class, ReportFormat.class);
    }

    @Test
    void combinationSubclassesAreGone() {
        for (String name : List.of("DailyCsvReport", "DailyTextReport", "WeeklyCsvReport", "WeeklyTextReport")) {
            assertThrows(ClassNotFoundException.class, () -> Class.forName(STEP3 + name), name);
        }
    }

    @Test
    void newFormatWorksWithEveryPeriodWithoutNewClasses() {
        ReportFormat markdown = new ReportFormat() {
            @Override
            public String header(String label) {
                return "## " + label + "\n";
            }

            @Override
            public String row(String title, int tickets, Money revenue) {
                return "- " + title + ": " + tickets + "\n";
            }

            @Override
            public String footer(int tickets, Money revenue) {
                return "**" + revenue + "**\n";
            }
        };
        List<ScreeningSale> sales = List.of(
                new ScreeningSale(LocalDate.parse("2026-10-19"), "Diuna", 2, Money.of("80.00")),
                new ScreeningSale(LocalDate.parse("2026-10-20"), "Diuna", 1, Money.of("40.00")));
        assertEquals("## dzien 2026-10-20\n- Diuna: 1\n**40.00**\n",
                new ScreeningReport(new DailyPeriod(), markdown).render(LocalDate.parse("2026-10-20"), sales));
        assertEquals("## tydzien 2026-10-19..2026-10-25\n- Diuna: 3\n**120.00**\n",
                new ScreeningReport(new WeeklyPeriod(), markdown).render(LocalDate.parse("2026-10-20"), sales));
    }

    @Test
    void beforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod() throws NoSuchMethodException {
        var render = pl.training.workshop.m5.s27_templatetostrategy.step2.ScreeningReport.class
                .getMethod("render", LocalDate.class, List.class);
        assertTrue(Modifier.isFinal(render.getModifiers()));
    }
}
