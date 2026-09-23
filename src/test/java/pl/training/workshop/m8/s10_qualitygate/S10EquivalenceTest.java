package pl.training.workshop.m8.s10_qualitygate;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Brak fałszywych alarmów: czysty kod przechodzi każdą wersję bramki. */
final class S10EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> cleanCodePassesEveryVersionOfTheGate() {
        return Scene.<GateInput, List<String>>variants()
                .variant("start", new pl.training.workshop.m8.s10_qualitygate.start.QualityGate()::evaluate)
                .variant("step1", new pl.training.workshop.m8.s10_qualitygate.step1.QualityGate()::evaluate)
                .variant("step2", new pl.training.workshop.m8.s10_qualitygate.step2.QualityGate()::evaluate)
                .variant("step3", new pl.training.workshop.m8.s10_qualitygate.step3.QualityGate()::evaluate)
                .variant("step4", new pl.training.workshop.m8.s10_qualitygate.step4.QualityGate()::evaluate)
                .expect("czysta próbka domeny", S10SolutionTest.CLEAN, List.of())
                .tests();
    }
}
