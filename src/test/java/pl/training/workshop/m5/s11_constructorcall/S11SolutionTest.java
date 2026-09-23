package pl.training.workshop.m5.s11_constructorcall;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Konstruktor wołający override: pułapka w start, naprawa lokalna (step1) i strukturalna (step2). */
final class S11SolutionTest {
    @Test
    void startSubclassSeesUninitializedField() {
        assertEquals("Miejsce K12 (VIP: null)", new pl.training.workshop.m5.s11_constructorcall.start.VipTicket("K12", "Salonik A").label());
    }

    @Test
    void constructorPrologInitializesFieldBeforeSuper() {
        assertEquals("Miejsce K12 (VIP: Salonik A)", new pl.training.workshop.m5.s11_constructorcall.step1.VipTicket("K12", "Salonik A").label());
    }

    @Test
    void solutionComputesLabelWhenObjectIsComplete() {
        assertEquals("Miejsce K12 (VIP: Salonik A)", new pl.training.workshop.m5.s11_constructorcall.step2.VipTicket("K12", "Salonik A").label());
    }
}
