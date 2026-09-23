package pl.training.workshop.m8.s09_codemod;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Na prostym przypadku (jedno wywołanie w jednej linii) wszystkie wersje codemodu się zgadzają.
 * Różnice wychodzą dopiero na trudnych przypadkach - patrz S09SolutionTest.
 */
final class S09EquivalenceTest {
    static final String SIMPLE = """
            package desk;

            import cinema.BookingService;

            public class Kiosk {
                String sell(BookingService bookings, String[] seats, String[] types) {
                    return bookings.book("S3", "anna@kino.pl", seats, types, true, false);
                }
            }
            """;

    @TestFactory
    Stream<DynamicTest> everyVersionHandlesTheSimpleCase() {
        return Scene.<String, String>variants()
                .variant("start", s -> describe(new pl.training.workshop.m8.s09_codemod.start.BookCallCodemod(
                        SampleProject.API).findLines(s), new pl.training.workshop.m8.s09_codemod.start.BookCallCodemod(
                        SampleProject.API).rewrite(s)))
                .variant("step1", s -> describe(new pl.training.workshop.m8.s09_codemod.step1.BookCallCodemod(
                        SampleProject.API).findLines(s), new pl.training.workshop.m8.s09_codemod.step1.BookCallCodemod(
                        SampleProject.API).rewrite(s)))
                .variant("step2", s -> describe(new pl.training.workshop.m8.s09_codemod.step2.BookCallCodemod(
                        SampleProject.API).findLines(s), new pl.training.workshop.m8.s09_codemod.step2.BookCallCodemod(
                        SampleProject.API).rewrite(s)))
                .variant("step3", s -> describe(new pl.training.workshop.m8.s09_codemod.step3.BookCallCodemod(
                        SampleProject.API).findLines(s), new pl.training.workshop.m8.s09_codemod.step3.BookCallCodemod(
                        SampleProject.API).rewrite(s)))
                .expect("jedno wywołanie z literałami", SIMPLE, """
                        linie: [7]
                        package desk;

                        import cinema.BookingService;
                        import cinema.Channel;
                        import cinema.Glasses;

                        public class Kiosk {
                            String sell(BookingService bookings, String[] seats, String[] types) {
                                return bookings.book("S3", "anna@kino.pl", seats, types, Channel.WEB, Glasses.RENTED);
                            }
                        }
                        """)
                .tests();
    }

    private static String describe(List<Integer> lines, String rewritten) {
        return "linie: " + lines + "\n" + rewritten;
    }
}
