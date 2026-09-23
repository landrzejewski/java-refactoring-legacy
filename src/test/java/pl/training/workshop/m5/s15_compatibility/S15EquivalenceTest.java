package pl.training.workshop.m5.s15_compatibility;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Test równoważności na poziomie ŹRÓDŁA: ten sam kod klienta, skompilowany z każdym wariantem,
 * daje ten sam eksport i tę samą wycenę. Zgodność binarną sprawdza S15SolutionTest.
 */
final class S15EquivalenceTest {
    @TestFactory
    Stream<DynamicTest> everyStepExportsTheSameRow() {
        return Scene.<String, String>variants()
                .variant("start", kind -> new pl.training.workshop.m5.s15_compatibility.start.TicketExporter().export(kind.equals("STUDENT")
                        ? new pl.training.workshop.m5.s15_compatibility.start.StudentTicket("Amator", Money.of("25.00"))
                        : new pl.training.workshop.m5.s15_compatibility.start.StandardTicket("Diuna", Money.of("40.00"))))
                .variant("step1", kind -> new pl.training.workshop.m5.s15_compatibility.step1.TicketExporter().export(kind.equals("STUDENT")
                        ? new pl.training.workshop.m5.s15_compatibility.step1.StudentTicket("Amator", Money.of("25.00"))
                        : new pl.training.workshop.m5.s15_compatibility.step1.StandardTicket("Diuna", Money.of("40.00"))))
                .variant("step2", kind -> new pl.training.workshop.m5.s15_compatibility.step2.TicketExporter().export(kind.equals("STUDENT")
                        ? new pl.training.workshop.m5.s15_compatibility.step2.StudentTicket("Amator", Money.of("25.00"))
                        : new pl.training.workshop.m5.s15_compatibility.step2.StandardTicket("Diuna", Money.of("40.00"))))
                .variant("step3", kind -> new pl.training.workshop.m5.s15_compatibility.step3.TicketExporter().export(kind.equals("STUDENT")
                        ? new pl.training.workshop.m5.s15_compatibility.step3.StudentTicket("Amator", Money.of("25.00"))
                        : new pl.training.workshop.m5.s15_compatibility.step3.StandardTicket("Diuna", Money.of("40.00"))))
                .variant("step4", kind -> new pl.training.workshop.m5.s15_compatibility.step4.TicketExporter().export(kind.equals("STUDENT")
                        ? new pl.training.workshop.m5.s15_compatibility.step4.StudentTicket("Amator", Money.of("25.00"))
                        : new pl.training.workshop.m5.s15_compatibility.step4.StandardTicket("Diuna", Money.of("40.00"))))
                .expect("studencki", "STUDENT", "Amator;cena=18.75")
                .expect("normalny", "NORMAL", "Diuna;cena=40.00")
                .tests();
    }

    @TestFactory
    @SuppressWarnings("deprecation")
    Stream<DynamicTest> everyStepQuotesStudentTicketTheSameWay() {
        return Scene.<String, String>variants()
                .variant("start", p -> new pl.training.workshop.m5.s15_compatibility.start.BoxOfficeApi()
                        .quote(new pl.training.workshop.m5.s15_compatibility.start.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step1", p -> new pl.training.workshop.m5.s15_compatibility.step1.BoxOfficeApi()
                        .quote(new pl.training.workshop.m5.s15_compatibility.step1.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step2", p -> new pl.training.workshop.m5.s15_compatibility.step2.BoxOfficeApi()
                        .quote(new pl.training.workshop.m5.s15_compatibility.step2.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step3", p -> new pl.training.workshop.m5.s15_compatibility.step3.BoxOfficeApi()
                        .quote(new pl.training.workshop.m5.s15_compatibility.step3.StudentTicket("Amator", Money.of(p))).toString())
                .variant("step4", p -> new pl.training.workshop.m5.s15_compatibility.step4.BoxOfficeApi()
                        .quote(new pl.training.workshop.m5.s15_compatibility.step4.StudentTicket("Amator", Money.of(p))).toString())
                .expect("studencki 2D", "25.00", "18.75")
                .tests();
    }
}
