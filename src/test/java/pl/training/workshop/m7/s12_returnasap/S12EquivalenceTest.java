package pl.training.workshop.m7.s12_returnasap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/**
 * Test równoważności: wynik firstFree, licznik inspected (efekt uboczny) oraz klasy miejsc.
 * Gdyby return trafił przed inspected++, licznik różniłby się o jeden - test to wykryje.
 */
final class S12EquivalenceTest {
    private static final List<Seat> HALL = List.of(
            new Seat("A1", 1, false),
            new Seat("A10", 10, true),
            new Seat("B10", 10, false),
            new Seat("C11", 11, false));

    record Query(List<Seat> seats, int minRow) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepFindsAndCountsTheSame() {
        return Scene.<Query, String>variants()
                .variant("start", q -> {
                    var finder = new pl.training.workshop.m7.s12_returnasap.start.SeatFinder();
                    return finder.firstFree(q.seats(), q.minRow()) + " sprawdzono=" + finder.inspected()
                            + " klasy=" + classes(seat -> finder.seatClass(seat, 10));
                })
                .variant("step1", q -> {
                    var finder = new pl.training.workshop.m7.s12_returnasap.step1.SeatFinder();
                    return finder.firstFree(q.seats(), q.minRow()) + " sprawdzono=" + finder.inspected()
                            + " klasy=" + classes(seat -> finder.seatClass(seat, 10));
                })
                .variant("step2", q -> {
                    var finder = new pl.training.workshop.m7.s12_returnasap.step2.SeatFinder();
                    return finder.firstFree(q.seats(), q.minRow()) + " sprawdzono=" + finder.inspected()
                            + " klasy=" + classes(seat -> finder.seatClass(seat, 10));
                })
                .expect("pierwsze wolne VIP po zajetym", new Query(HALL, 10),
                        "Optional[B10] sprawdzono=3 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
                .expect("pierwsze miejsce od razu", new Query(HALL, 1),
                        "Optional[A1] sprawdzono=1 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
                .expect("brak pasujacego - przejrzane wszystkie", new Query(HALL, 12),
                        "Optional.empty sprawdzono=4 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
                .expect("lista null", new Query(null, 1),
                        "Optional.empty sprawdzono=0 klasy=[STANDARD, ZAJETE, VIP, VIP, BRAK]")
                .tests();
    }

    private static List<String> classes(Function<Seat, String> seatClass) {
        List<Seat> probes = new ArrayList<>(HALL);
        probes.add(null);
        return probes.stream().map(seatClass).toList();
    }
}
