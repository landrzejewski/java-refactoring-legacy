package pl.training.workshop.m4.s11_encapsulatecollection;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: przez SeatDesk wszystkie warianty dają te same miejsca i tę samą kwotę. */
final class S11EquivalenceTest {
    /** Polecenie kasy: +rząd/numer wybiera miejsce, -rząd/numer je zwalnia. */
    private static final List<String> COMMANDS = List.of("+1/5", "+10/3", "-1/5", "+12/1", "+10/3");

    @TestFactory
    Stream<DynamicTest> everyVariantSelectsSeatsTheSameWay() {
        return Scene.<List<String>, String>variants()
                .variant("start", commands -> {
                    var desk = new pl.training.workshop.m4.s11_encapsulatecollection.start.SeatDesk();
                    var booking = new pl.training.workshop.m4.s11_encapsulatecollection.start.Booking();
                    return run(commands, (seat, add) -> {
                        if (add) {
                            desk.select(booking, seat);
                        } else {
                            desk.release(booking, seat);
                        }
                    }, () -> booking.seats + " -> " + booking.total());
                })
                .variant("step1", commands -> {
                    var desk = new pl.training.workshop.m4.s11_encapsulatecollection.step1.SeatDesk();
                    var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step1.Booking();
                    return run(commands, (seat, add) -> {
                        if (add) {
                            desk.select(booking, seat);
                        } else {
                            desk.release(booking, seat);
                        }
                    }, () -> booking.seats() + " -> " + booking.total());
                })
                .variant("step2", commands -> {
                    var desk = new pl.training.workshop.m4.s11_encapsulatecollection.step2.SeatDesk();
                    var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step2.Booking();
                    return run(commands, (seat, add) -> {
                        if (add) {
                            desk.select(booking, seat);
                        } else {
                            desk.release(booking, seat);
                        }
                    }, () -> booking.seats() + " -> " + booking.total());
                })
                .variant("step3", commands -> {
                    var desk = new pl.training.workshop.m4.s11_encapsulatecollection.step3.SeatDesk();
                    var booking = new pl.training.workshop.m4.s11_encapsulatecollection.step3.Booking();
                    return run(commands, (seat, add) -> {
                        if (add) {
                            desk.select(booking, seat);
                        } else {
                            desk.release(booking, seat);
                        }
                    }, () -> booking.seats() + " -> " + booking.total());
                })
                .expect("wybór, zwolnienie, duplikat zostaje (lista, nie zbiór)", COMMANDS,
                        "[10/3, 12/1, 10/3] -> 105.00")
                .expect("pusta rezerwacja", List.of(), "[] -> 0.00")
                .expect("zwolnienie miejsca, którego nie ma", List.of("+1/1", "-2/2"), "[1/1] -> 25.00")
                .tests();
    }

    private static String run(List<String> commands, BiConsumer<Seat, Boolean> desk, Supplier<String> result) {
        for (String command : commands) {
            String[] parts = command.substring(1).split("/");
            desk.accept(new Seat(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), command.startsWith("+"));
        }
        return result.get();
    }
}
