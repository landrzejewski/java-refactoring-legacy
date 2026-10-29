package pl.training.workshop.exercises.e10_showtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Sens ćwiczenia: kopia orkiestracji w automacie pomija raport frekwencji. Fasada w kroku 2
 * to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja, dlatego ma osobny test.
 */
final class E10BehaviourChangeTest {
    private static final List<String> AUTO_START = List.of("scan S1", "scan S1", "auto start S1 Diuna", "tick", "tick");

    private static final String WITHOUT_REPORT = """
            scanner: S1 closed, 2 scanned
            doors: closed
            lights: 30%
            ads: start 2
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna""";

    private static final String WITH_REPORT = """
            scanner: S1 closed, 2 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 2
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna""";

    /** Po "warsztat.sh jump" do kroku 2 automat nie ma już własnej orkiestracji i test jest pomijany. */
    @Test
    void startAutoSchedulerSkipsTheAttendanceReport() {
        boolean ownOrchestration = Arrays.stream(
                        pl.training.workshop.exercises.e10_showtime.start.AutoScheduler.class.getDeclaredFields())
                .anyMatch(field -> field.getType().getSimpleName().equals("TicketScanner"));
        assumeTrue(ownOrchestration, "start automatu korzysta już z fasady");
        assertEquals(WITHOUT_REPORT, new pl.training.workshop.exercises.e10_showtime.start.ShowtimeRunner().run(AUTO_START));
    }

    @Test
    void step1StillSkipsTheReportBecauseOnlyTheHallChanged() {
        assertEquals(WITHOUT_REPORT, new pl.training.workshop.exercises.e10_showtime.step1.ShowtimeRunner().run(AUTO_START));
    }

    @Test
    void step2FacadeReportsAttendanceForBothClients() {
        assertEquals(WITH_REPORT, new pl.training.workshop.exercises.e10_showtime.step2.ShowtimeRunner().run(AUTO_START));
        assertEquals(WITH_REPORT, new pl.training.workshop.exercises.e10_showtime.step2.ShowtimeRunner()
                .run(List.of("scan S1", "scan S1", "desk start S1 Diuna", "tick", "tick")));
    }
}
