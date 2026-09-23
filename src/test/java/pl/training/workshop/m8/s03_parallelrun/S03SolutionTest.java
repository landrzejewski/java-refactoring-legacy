package pl.training.workshop.m8.s03_parallelrun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.EVENING_2D_SENIOR;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.IMAX_NORMAL;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.IMAX_STUDENT_VIP;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.MORNING_2D_STUDENT;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.MORNING_3D_CHILD;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.MORNING_3D_NORMAL_VIP;
import static pl.training.workshop.m8.s03_parallelrun.S03EquivalenceTest.UNKNOWN_4DX;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import pl.training.workshop.shared.Money;

/** Co wiemy po każdym kroku trybu shadow i co zmienia przełączenie. */
final class S03SolutionTest {
    private static final List<TicketQuery> TRAFFIC = List.of(IMAX_NORMAL, IMAX_STUDENT_VIP, MORNING_3D_CHILD,
            MORNING_3D_NORMAL_VIP, EVENING_2D_SENIOR, MORNING_2D_STUDENT, UNKNOWN_4DX);

    @Test
    void step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches() {
        var service = new pl.training.workshop.m8.s03_parallelrun.step1.PriceService();
        replay(service::price);
        assertEquals(Money.of("0.00"), service.price(UNKNOWN_4DX), "klient dostaje wynik legacy");
        assertEquals(4, service.mismatches(), "3 różnice w ruchu + powtórzony 4DX, ale nie wiemy które");
    }

    @Test
    void step2ReportShowsWhatDivergedAndWhy() {
        var report = new pl.training.workshop.m8.s03_parallelrun.step2.VerificationReport();
        replay(new pl.training.workshop.m8.s03_parallelrun.step2.PriceService(report)::price);
        assertEquals(List.of(
                "ROZBIEZNOSC 3D CHILD 10:30 rzad 3: legacy 17.20, kandydat 19.20",
                "ROZBIEZNOSC 2D STUDENT 09:00 rzad 2: legacy 13.75, kandydat 15.00",
                "BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: IllegalArgumentException: Nieznany format: 4DX"),
                report.problems());
        assertEquals(TRAFFIC.size(), report.entries().size());
    }

    @Test
    void step3FixedCandidateLeavesOnlyTheAcceptedDifference() {
        var report = new pl.training.workshop.m8.s03_parallelrun.step3.VerificationReport();
        replay(new pl.training.workshop.m8.s03_parallelrun.step3.PriceService(report)::price);
        assertEquals(List.of(
                "BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: IllegalArgumentException: Nieznany format: 4DX"),
                report.problems());
    }

    @Test
    void step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat() {
        var report = new pl.training.workshop.m8.s03_parallelrun.step4.VerificationReport();
        var service = new pl.training.workshop.m8.s03_parallelrun.step4.PriceService(
                pl.training.workshop.m8.s03_parallelrun.step4.MigrationMode.CANDIDATE, report);
        assertEquals(Money.of("17.20"), service.price(MORNING_3D_CHILD));
        assertThrows(IllegalArgumentException.class, () -> service.price(UNKNOWN_4DX));
        assertTrue(report.entries().isEmpty(), "po przełączeniu cień już nie działa");
    }

    private static void replay(Consumer<TicketQuery> service) {
        TRAFFIC.forEach(service);
    }
}
