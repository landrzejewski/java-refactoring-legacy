package pl.training.workshop.m5.s10_fieldhiding;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Wspólna część wariantów: bilet normalny opisuje się tak samo w każdym kroku. */
final class S10EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepLabelsNormalTicketTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", ignored -> new pl.training.workshop.m5.s10_fieldhiding.start.Ticket().label())
                .variant("step1", ignored -> new pl.training.workshop.m5.s10_fieldhiding.step1.Ticket().label())
                .variant("step2", ignored -> new pl.training.workshop.m5.s10_fieldhiding.step2.Ticket().label())
                .expect("bilet normalny", "", "BILET: NORMAL")
                .tests();
    }
}
