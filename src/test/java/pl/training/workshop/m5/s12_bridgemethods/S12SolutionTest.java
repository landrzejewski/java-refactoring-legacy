package pl.training.workshop.m5.s12_bridgemethods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Metody bridge: niewidoczne w źródle, widoczne dla refleksji i dla surowych wywołań. */
final class S12SolutionTest {
    @Test
    void genericInterfaceAddsSyntheticBridgeMethod() {
        List<Method> apply = applyMethods(pl.training.workshop.m5.s12_bridgemethods.step1.StudentRule.class);
        assertEquals(2, apply.size());
        Method bridge = apply.stream().filter(Method::isBridge).findFirst().orElseThrow();
        assertEquals(Ticket.class, bridge.getParameterTypes()[0], "erasure T extends Ticket = Ticket");
    }

    @Test
    void naiveReflectionWouldRegisterErasedType() {
        List<String> seenByNaiveScan = Arrays.stream(pl.training.workshop.m5.s12_bridgemethods.step1.StudentRule.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("apply"))
                .map(m -> m.getParameterTypes()[0].getSimpleName())
                .sorted()
                .toList();
        assertEquals(List.of("StudentTicket", "Ticket"), seenByNaiveScan);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void rawCallThroughBridgeFailsWithClassCastException() {
        pl.training.workshop.m5.s12_bridgemethods.step1.PriceRule raw = new pl.training.workshop.m5.s12_bridgemethods.step1.StudentRule();
        assertThrows(ClassCastException.class, () -> raw.apply(new StandardTicket(Money.of("25.00"))));
    }

    @Test
    void solutionReportsMissingRuleExplicitly() {
        var registry = new pl.training.workshop.m5.s12_bridgemethods.step2.RuleRegistry(new pl.training.workshop.m5.s12_bridgemethods.step2.StandardRule());
        assertThrows(IllegalStateException.class,
                () -> registry.price(new StudentTicket(Money.of("25.00"), "S-1")));
    }

    private static List<Method> applyMethods(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods()).filter(m -> m.getName().equals("apply")).toList();
    }
}
