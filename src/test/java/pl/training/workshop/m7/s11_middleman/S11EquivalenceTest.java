package pl.training.workshop.m7.s11_middleman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności obu klientów pośrednika - także dla nieznanego seansu. */
final class S11EquivalenceTest {
    private static final ScreeningCatalog CATALOG = new ScreeningCatalog(List.of(
            new Screening("S1", "Diuna", "IMAX", 120),
            new Screening("S2", "Kraina Lodu", "3D", 0),
            new Screening("S3", "Amator", "2D", 7)));

    private static final String BOARD = "S1 Diuna IMAX\nS2 Kraina Lodu 3D\nS3 Amator 2D";

    @TestFactory
    Stream<DynamicTest> clientsSeeTheSameThing() {
        var startFacade = new pl.training.workshop.m7.s11_middleman.start.CinemaFacade(CATALOG);
        var step1Facade = new pl.training.workshop.m7.s11_middleman.step1.CinemaFacade(CATALOG);
        var step2Facade = new pl.training.workshop.m7.s11_middleman.step2.CinemaFacade(CATALOG);
        return Scene.<String, String>variants()
                .variant("start", id -> new pl.training.workshop.m7.s11_middleman.start.SeatBadge(startFacade).badge(id)
                        + " | " + new pl.training.workshop.m7.s11_middleman.start.DailyBoard(startFacade).render())
                .variant("step1", id -> new pl.training.workshop.m7.s11_middleman.step1.SeatBadge(step1Facade).badge(id)
                        + " | " + new pl.training.workshop.m7.s11_middleman.step1.DailyBoard(step1Facade).render())
                .variant("step2", id -> new pl.training.workshop.m7.s11_middleman.step2.SeatBadge(CATALOG).badge(id)
                        + " | " + new pl.training.workshop.m7.s11_middleman.step2.DailyBoard(step2Facade).render())
                .variant("step3", id -> new pl.training.workshop.m7.s11_middleman.step3.SeatBadge(CATALOG).badge(id)
                        + " | " + new pl.training.workshop.m7.s11_middleman.step3.DailyBoard(CATALOG).render())
                .expect("wolne miejsca", "S1", "Diuna (IMAX): 120 wolnych | " + BOARD)
                .expect("wyprzedane", "S2", "S2: WYPRZEDANE | " + BOARD)
                .expect("nieznany seans wyglada jak wyprzedany (historyczne zachowanie)", "S9",
                        "S9: WYPRZEDANE | " + BOARD)
                .tests();
    }

    @Test
    void catalogItselfRejectsUnknownScreening() {
        NoSuchElementException error = assertThrows(NoSuchElementException.class, () -> CATALOG.freeSeats("S9"));
        assertEquals("brak seansu S9", error.getMessage());
    }
}
