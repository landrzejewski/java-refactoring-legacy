package pl.training.workshop.m6.s07_decorator;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/** Cena i opis (z kolejnością dodatków) takie same w każdym kroku. */
final class S07EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepPricesEmbellishmentsTheSame() {
        return Scene.<TicketOrder, String>variants()
                .variant("start", o -> {
                    var t = new pl.training.workshop.m6.s07_decorator.start.TicketAssembler().assemble(o);
                    return t.description() + " = " + t.price();
                })
                .variant("step1", o -> {
                    var t = new pl.training.workshop.m6.s07_decorator.step1.TicketAssembler().assemble(o);
                    return t.description() + " = " + t.price();
                })
                .variant("step2", o -> {
                    var t = new pl.training.workshop.m6.s07_decorator.step2.TicketAssembler().assemble(o);
                    return t.description() + " = " + t.price();
                })
                .variant("step3", o -> {
                    var t = new pl.training.workshop.m6.s07_decorator.step3.TicketAssembler().assemble(o);
                    return t.description() + " = " + t.price();
                })
                .expect("bez dodatków", order("Amator", "2D", "25.00", false, false, false), "Amator 2D = 25.00")
                .expect("3D z okularami kina", order("Kraina Lodu", "3D", "32.00", false, false, false),
                        "Kraina Lodu 3D +okulary 3D = 35.00")
                .expect("3D z własnymi okularami", order("Kraina Lodu", "3D", "32.00", false, true, false),
                        "Kraina Lodu 3D = 32.00")
                .expect("wszystkie dodatki", order("Kraina Lodu", "3D", "32.00", true, false, true),
                        "Kraina Lodu 3D +VIP +okulary 3D +ubezpieczenie = 49.00")
                .expect("IMAX VIP z ubezpieczeniem", order("Diuna", "IMAX", "40.00", true, false, true),
                        "Diuna IMAX +VIP +ubezpieczenie = 54.00")
                .tests();
    }

    private static TicketOrder order(String title, String format, String base,
                                     boolean vip, boolean ownGlasses, boolean insurance) {
        return new TicketOrder(title, format, Money.of(base), vip, ownGlasses, insurance);
    }
}
