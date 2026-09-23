package pl.training.workshop.m5.s08_composition;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności na pojedynczych kliknięciach (add), gdzie start działa poprawnie.
 * Hurtowe addAll() różni się celowo - patrz S08SolutionTest.
 */
final class S08EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepCountsSingleClicksTheSameWay() {
        return Scene.<List<String>, String>variants()
                .variant("start", seats -> {
                    var selection = new pl.training.workshop.m5.s08_composition.start.SeatSelection();
                    seats.forEach(selection::add);
                    return selection.size() + " miejsc, " + selection.clicks() + " kliknięć";
                })
                .variant("step1", seats -> {
                    var selection = new pl.training.workshop.m5.s08_composition.step1.SeatSelection();
                    seats.forEach(selection::add);
                    return selection.size() + " miejsc, " + selection.clicks() + " kliknięć";
                })
                .variant("step2", seats -> {
                    var selection = new pl.training.workshop.m5.s08_composition.step2.SeatSelection();
                    seats.forEach(selection::add);
                    return selection.size() + " miejsc, " + selection.clicks() + " kliknięć";
                })
                .expect("dwa miejsca", List.of("H7", "H8"), "2 miejsc, 2 kliknięć")
                .expect("ponowne kliknięcie tego samego miejsca", List.of("H7", "H8", "H7"), "2 miejsc, 3 kliknięć")
                .expect("nic nie wybrano", List.of(), "0 miejsc, 0 kliknięć")
                .tests();
    }
}
