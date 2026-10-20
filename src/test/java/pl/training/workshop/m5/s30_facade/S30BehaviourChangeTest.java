package pl.training.workshop.m5.s30_facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

/**
 * Sens sceny: kopia orkiestracji w kasie zapomniała zwolnić miejsca po odmowie płatności.
 * Fasada w kroku 2 to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja.
 */
final class S30BehaviourChangeTest {
    private static final List<String> DECLINED_AT_DESK = List.of(
            "desk S1 A1 N 10000 jan@example.com", "state", "online S1 A1 N 4111 anna@example.com");

    private static final String LEAK = """
            ERROR payment declined
            miejsca={S1=[A1]} platnosci=[DECLINED 10000 25.00] maile=[]
            ERROR seat taken A1""";

    private static final String RELEASED = """
            ERROR payment declined
            miejsca={S1=[]} platnosci=[DECLINED 10000 25.00] maile=[]
            OK 27.00""";

    /** Po "warsztat.sh jump" do kroku 2+ kasa nie ma już własnej orkiestracji i test jest pomijany. */
    @Test
    void startBoxOfficeLeaksSeatsAfterDeclinedPayment() {
        boolean ownOrchestration = Arrays.stream(
                        pl.training.workshop.m5.s30_facade.start.BoxOffice.class.getDeclaredFields())
                .anyMatch(field -> field.getType().getSimpleName().equals("SeatRegistry"));
        assumeTrue(ownOrchestration, "start kasy korzysta już z fasady");
        var cinema = new pl.training.workshop.m5.s30_facade.start.Cinema();
        assertEquals(LEAK, play(S30EquivalenceTest.channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell,
                cinema::state)));
    }

    @Test
    void step1StillLeaksBecauseOnlyTheShopUsesTheFacade() {
        var cinema = new pl.training.workshop.m5.s30_facade.step1.Cinema();
        assertEquals(LEAK, play(S30EquivalenceTest.channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell,
                cinema::state)));
    }

    @Test
    void step2FacadeReleasesSeatsForBothChannels() {
        var cinema = new pl.training.workshop.m5.s30_facade.step2.Cinema();
        assertEquals(RELEASED, play(S30EquivalenceTest.channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell,
                cinema::state)));
    }

    @Test
    void step3FacadeReleasesSeatsForBothChannels() {
        var cinema = new pl.training.workshop.m5.s30_facade.step3.Cinema();
        assertEquals(RELEASED, play(S30EquivalenceTest.channels(cinema.onlineShop()::buy, cinema.boxOffice()::sell,
                cinema::state)));
    }

    private static String play(Function<String, String> handle) {
        return String.join("\n", DECLINED_AT_DESK.stream().map(handle).toList());
    }
}
