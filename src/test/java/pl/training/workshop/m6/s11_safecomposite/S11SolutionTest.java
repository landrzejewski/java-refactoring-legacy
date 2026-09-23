package pl.training.workshop.m6.s11_safecomposite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

/** Transparent kontra Safe: gdzie wychodzi błąd add() na liściu. */
final class S11SolutionTest {
    /** Dokumentuje pułapkę start; po "warsztat.sh jump" (start = krok 1+) test jest pomijany. */
    @Test
    void transparentCompositeFailsAtRuntime() throws ReflectiveOperationException {
        String start = "pl.training.workshop.m6.s11_safecomposite.start.";
        Class<?> component = Class.forName(start + "MenuComponent");
        Class<?> product = Class.forName(start + "Product");
        Method add = findMethod(product, "add", component);
        assumeTrue(add != null, "start nie jest już Transparent Composite");
        Object nachos = product.getConstructor(String.class, String.class).newInstance("Nachos", "14.00");
        Object sauce = product.getConstructor(String.class, String.class).newInstance("Sos", "3.00");
        var error = assertThrows(InvocationTargetException.class, () -> add.invoke(nachos, sauce));
        assertEquals(UnsupportedOperationException.class, error.getCause().getClass());
        assertEquals("cannot add to Nachos", error.getCause().getMessage());
    }

    private static Method findMethod(Class<?> type, String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }

    @Test
    void safeCompositeHasNoAddOnLeafOrCommonType() {
        String step1 = "pl.training.workshop.m6.s11_safecomposite.step1.";
        assertThrows(NoSuchMethodException.class, () -> Class.forName(step1 + "Product")
                .getMethod("add", Class.forName(step1 + "MenuComponent")));
        assertThrows(NoSuchMethodException.class, () -> Class.forName(step1 + "MenuComponent")
                .getMethod("children"));
    }

    @Test
    void immutableCompositeHasNoAddAtAll() {
        var combo = pl.training.workshop.m6.s11_safecomposite.step2.Combo.of("Zestaw");
        assertThrows(UnsupportedOperationException.class, () -> combo.children().add(null));
    }
}
