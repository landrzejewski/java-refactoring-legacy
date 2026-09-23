package pl.training.workshop.m3.s05_kiss;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Prostsza wersja liczy wolne miejsca dokładnie tak samo jak "sprytna". */
final class S05EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepCountsFreeSeatsTheSame() {
        return Scene.<Hall, String>variants()
                .variant("start", new pl.training.workshop.m3.s05_kiss.start.SeatCounter()::summary)
                .variant("step1", new pl.training.workshop.m3.s05_kiss.step1.SeatCounter()::summary)
                .variant("step2", new pl.training.workshop.m3.s05_kiss.step2.SeatCounter()::summary)
                .expect("zajete, zablokowane i przejscie nie sa wolne",
                        new Hall(List.of("..X", "XXX", ".X.", ". B."), 3), "wolne: 6, wolne VIP: 4")
                .expect("sala bez rzedow VIP", new Hall(List.of("....", "...."), 10), "wolne: 8, wolne VIP: 0")
                .expect("cala sala VIP", new Hall(List.of("X.", ".X"), 1), "wolne: 2, wolne VIP: 2")
                .expect("pusta lista rzedow", new Hall(List.of(), 10), "wolne: 0, wolne VIP: 0")
                .tests();
    }
}
