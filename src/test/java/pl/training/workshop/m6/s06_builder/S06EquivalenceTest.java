package pl.training.workshop.m6.s06_builder;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Repertuar dnia zbudowany przez każdy wariant renderuje się identycznie. */
final class S06EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepBuildsTheSameSchedule() {
        return Scene.<LocalDate, String>variants()
                .variant("start", d -> new pl.training.workshop.m6.s06_builder.start.WeekendPlanner().plan(d).render())
                .variant("step1", d -> new pl.training.workshop.m6.s06_builder.step1.WeekendPlanner().plan(d).render())
                .variant("step2", d -> new pl.training.workshop.m6.s06_builder.step2.WeekendPlanner().plan(d).render())
                .variant("step3", d -> new pl.training.workshop.m6.s06_builder.step3.WeekendPlanner().plan(d).render())
                .expect("sobota", LocalDate.of(2026, 10, 3), """
                        2026-10-03 SATURDAY
                        Sala 1
                          18:00 Diuna
                          21:00 Diuna
                        Sala 2
                          10:00 Kraina Lodu
                          17:30 Amator
                        Sala 3 VIP
                          20:00 Amator
                        Seansow: 5
                        """)
                .expect("piątek - pusta sala VIP", LocalDate.of(2026, 10, 2), """
                        2026-10-02 FRIDAY
                        Sala 1
                          18:00 Diuna
                          21:00 Diuna
                        Sala 2
                          17:30 Amator
                        Sala 3 VIP
                          (brak seansow)
                        Seansow: 3
                        """)
                .tests();
    }
}
