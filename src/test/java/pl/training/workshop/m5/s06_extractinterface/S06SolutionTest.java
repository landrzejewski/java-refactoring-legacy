package pl.training.workshop.m5.s06_extractinterface;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Rola klienta zamiast wielu przeciążeń; metoda domyślna rozszerza kontrakt bez łamania implementacji. */
final class S06SolutionTest {
    /** Nowa implementacja spoza pierwotnej hierarchii: okulary 3D (+3.00, VAT 23%). */
    record Glasses3D() implements pl.training.workshop.m5.s06_extractinterface.step3.Priceable {
        @Override
        public Money price() {
            return Money.of("3.00");
        }

        @Override
        public int vatPercent() {
            return 23;
        }
    }

    @Test
    void beforeMigrationCartHasOneAddPerConcreteType() {
        assertEquals(2, Arrays.stream(pl.training.workshop.m5.s06_extractinterface.step1.Cart.class.getMethods())
                .filter(m -> m.getName().equals("add")).count());
    }

    @Test
    void solutionCartDependsOnlyOnTheRole() throws Exception {
        assertEquals(void.class, pl.training.workshop.m5.s06_extractinterface.step3.Cart.class.getMethod("add", pl.training.workshop.m5.s06_extractinterface.step3.Priceable.class).getReturnType());
        assertTrue(pl.training.workshop.m5.s06_extractinterface.step3.Priceable.class.getMethod("vatAmount").isDefault());
    }

    @Test
    void newImplementationGetsDefaultMethodForFree() {
        assertEquals(Money.of("0.56"), new Glasses3D().vatAmount());
        var cart = new pl.training.workshop.m5.s06_extractinterface.step3.Cart();
        cart.add(new pl.training.workshop.m5.s06_extractinterface.step3.Ticket("Kraina Lodu", "B4", Money.of("32.00")));
        cart.add(new Glasses3D());
        assertEquals("Razem: 35.00, VAT: 2.93", cart.summary());
    }
}
