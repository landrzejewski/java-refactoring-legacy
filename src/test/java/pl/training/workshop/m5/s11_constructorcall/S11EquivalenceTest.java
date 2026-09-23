package pl.training.workshop.m5.s11_constructorcall;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Wspólna część wariantów: zwykły bilet (bez podklasy) ma tę samą etykietę w każdym kroku. */
final class S11EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepLabelsPlainTicketTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", seat -> new pl.training.workshop.m5.s11_constructorcall.start.Ticket(seat).label())
                .variant("step1", seat -> new pl.training.workshop.m5.s11_constructorcall.step1.Ticket(seat).label())
                .variant("step2", seat -> new pl.training.workshop.m5.s11_constructorcall.step2.Ticket(seat).label())
                .expect("zwykłe miejsce", "H7", "Miejsce H7")
                .tests();
    }
}
