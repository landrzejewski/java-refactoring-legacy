package pl.training.workshop.m8.s04_shadowlimits;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Efekty uboczne w trybie shadow: od podwójnych maili i obciążeń do porównania zamiarów. */
final class S04SolutionTest {
    private static final BookingRequest ANNA = new BookingRequest("anna@kino.pl", "4111-1111", "Amator", 2);
    private static final List<String> LEGACY_EFFECTS = List.of(
            "CHARGE 4111-1111: 54.00",
            "SAVE Amator;anna@kino.pl;2;54.00",
            "MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00");
    private static final String MAIL_DIVERGENCE = "efekty legacy: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 54.00"
            + " | kandydat: MAIL anna@kino.pl: Bilety Amator x2, zaplacono 50.00";

    @Test
    void startShadowChargesTwiceAndSendsTwoMailsWithoutNoticingAnything() {
        Infrastructure infra = new Infrastructure();
        var shadow = new pl.training.workshop.m8.s04_shadowlimits.start.ShadowBooking(infra);
        shadow.book(ANNA);
        assertEquals(2, infra.count("MAIL"), "klient dostał dwa maile");
        assertEquals(2, infra.count("CHARGE"), "karta obciążona dwa razy");
        assertEquals(2, infra.count("SAVE"), "dwa wiersze w bazie");
        assertEquals(List.of(), shadow.divergences(), "wynik ten sam, więc cień niczego nie widzi");
    }

    @Test
    void step1PreparatoryRefactoringKeepsTheBugOnPurpose() {
        Infrastructure infra = new Infrastructure();
        new pl.training.workshop.m8.s04_shadowlimits.step1.ShadowBooking(infra).book(ANNA);
        assertEquals(2, infra.count("MAIL"));
        assertEquals(2, infra.count("CHARGE"));
    }

    @Test
    void step2RecordsCandidateEffectsInsteadOfExecutingThem() {
        Infrastructure infra = new Infrastructure();
        var shadow = new pl.training.workshop.m8.s04_shadowlimits.step2.ShadowBooking(infra);
        shadow.book(ANNA);
        assertEquals(LEGACY_EFFECTS, infra.log(), "tylko efekty legacy");
        assertEquals(List.of(MAIL_DIVERGENCE), shadow.divergences(), "porównanie efektów znalazło błąd w mailu");
    }

    @Test
    void step3ComparesAPurePlanAndNeverTouchesInfrastructure() {
        Infrastructure infra = new Infrastructure();
        var shadow = new pl.training.workshop.m8.s04_shadowlimits.step3.ShadowBooking(infra);
        shadow.book(ANNA);
        assertEquals(LEGACY_EFFECTS, infra.log());
        assertEquals(List.of(MAIL_DIVERGENCE), shadow.divergences());
    }

    @Test
    void step3NewFlowStillExecutesItsPlanWhenItIsAuthoritative() {
        Infrastructure infra = new Infrastructure();
        var flow = new pl.training.workshop.m8.s04_shadowlimits.step3.NewBookingFlow(
                new pl.training.workshop.m8.s04_shadowlimits.step3.RealEffects(infra));
        assertEquals("OK 54.00", flow.book(ANNA));
        assertEquals(3, infra.log().size());
    }
}
