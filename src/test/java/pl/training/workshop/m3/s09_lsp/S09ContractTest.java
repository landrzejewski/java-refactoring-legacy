package pl.training.workshop.m3.s09_lsp;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Testy kontraktowe: ten sam zestaw sprawdzeń dla KAŻDEJ implementacji danego typu.
 * Dają dowody zgodności (LSP) dla sprawdzonych stanów - nie formalny dowód.
 */
final class S09ContractTest {
    /** Widok testu na salę z dowolnego wariantu (w wariantach to różne klasy o tych samych nazwach). */
    interface HallUnderTest {
        void reserve(int seat);

        boolean isFree(int seat);

        int freeSeats();

        int capacity();
    }

    /** Kontrakt Hall.reserve: wolne miejsce zostaje zajęte, licznik maleje, druga rezerwacja - błąd. */
    static void obeysReserveContract(HallUnderTest hall) {
        int before = hall.freeSeats();
        assertTrue(hall.isFree(3));
        hall.reserve(3);
        assertAll(
                () -> assertFalse(hall.isFree(3)),
                () -> assertEquals(before - 1, hall.freeSeats()),
                () -> assertThrows(IllegalStateException.class, () -> hall.reserve(3)));
    }

    /** Kontrakt odczytu (SeatMap): freeSeats zgodne z isFree dla wszystkich miejsc. */
    static void obeysReadContract(HallUnderTest seats) {
        long free = IntStream.rangeClosed(1, seats.capacity()).filter(seats::isFree).count();
        assertEquals(free, seats.freeSeats());
    }

    @TestFactory
    Stream<DynamicTest> everyHallObeysTheReserveContract() {
        Map<String, Supplier<HallUnderTest>> halls = new LinkedHashMap<>();
        halls.put("start: Hall", () -> of(new pl.training.workshop.m3.s09_lsp.start.Hall(5)));
        halls.put("step1: Hall", () -> of(new pl.training.workshop.m3.s09_lsp.step1.Hall(5)));
        halls.put("step2: Hall", () -> of(new pl.training.workshop.m3.s09_lsp.step2.Hall(5)));
        // step2.ReadOnlyHall nie jest już Hall - kompilator nie pozwala dodać go do tej listy.
        return halls.entrySet().stream().map(e ->
                DynamicTest.dynamicTest(e.getKey(), () -> obeysReserveContract(e.getValue().get())));
    }

    @TestFactory
    Stream<DynamicTest> everySeatMapObeysTheReadContract() {
        Map<String, Supplier<HallUnderTest>> maps = new LinkedHashMap<>();
        maps.put("step1: ReadOnlyHall", () -> of(new pl.training.workshop.m3.s09_lsp.step1.ReadOnlyHall(5, Set.of(1, 4))));
        maps.put("step2: ReadOnlyHall", () -> of(new pl.training.workshop.m3.s09_lsp.step2.ReadOnlyHall(5, Set.of(1, 4))));
        maps.put("step2: Hall", () -> of(new pl.training.workshop.m3.s09_lsp.step2.Hall(5)));
        return maps.entrySet().stream().map(e ->
                DynamicTest.dynamicTest(e.getKey(), () -> obeysReadContract(e.getValue().get())));
    }

    @Test
    void readOnlyHallAsSubclassBreaksTheReserveContract() {
        // krok 1 = stan z start dla ReadOnlyHall (start jest edytowany na żywo, więc sprawdzamy kopię)
        var archived = new pl.training.workshop.m3.s09_lsp.step1.ReadOnlyHall(5, Set.of());
        assertThrows(UnsupportedOperationException.class, () -> obeysReserveContract(of(archived)));
    }

    // start jest edytowany na żywo - test dotyka go tylko przez API wspólne dla wszystkich kroków.
    private static HallUnderTest of(pl.training.workshop.m3.s09_lsp.start.Hall hall) {
        return new HallUnderTest() {
            public void reserve(int seat) { hall.reserve(seat); }
            public boolean isFree(int seat) { return hall.isFree(seat); }
            public int freeSeats() { return hall.freeSeats(); }
            public int capacity() { return hall.capacity(); }
        };
    }

    private static HallUnderTest of(pl.training.workshop.m3.s09_lsp.step1.Hall hall) {
        return new HallUnderTest() {
            public void reserve(int seat) { hall.reserve(seat); }
            public boolean isFree(int seat) { return hall.isFree(seat); }
            public int freeSeats() { return hall.freeSeats(); }
            public int capacity() { return hall.capacity(); }
        };
    }

    private static HallUnderTest of(pl.training.workshop.m3.s09_lsp.step2.Hall hall) {
        return new HallUnderTest() {
            public void reserve(int seat) { hall.reserve(seat); }
            public boolean isFree(int seat) { return hall.isFree(seat); }
            public int freeSeats() { return hall.freeSeats(); }
            public int capacity() { return hall.capacity(); }
        };
    }

    /** Sala archiwalna z kroku 2 ma tylko rolę odczytu - reserve w widoku testu jest niedostępne. */
    private static HallUnderTest of(pl.training.workshop.m3.s09_lsp.step2.ReadOnlyHall seats) {
        return new HallUnderTest() {
            public void reserve(int seat) { throw new AssertionError("SeatMap nie ma reserve"); }
            public boolean isFree(int seat) { return seats.isFree(seat); }
            public int freeSeats() { return seats.freeSeats(); }
            public int capacity() { return seats.capacity(); }
        };
    }
}
