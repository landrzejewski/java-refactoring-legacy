package pl.training.workshop.m4.s05_inlinevariable;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: te same bilety (numery, etykiety, czasy) po każdym kroku. */
final class S05EquivalenceTest {
    static final Instant T0 = Instant.parse("2026-09-25T18:00:00Z");

    record Request(String screeningCode, int format) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepIssuesTheSameTickets() {
        return Scene.<List<Request>, List<Ticket>>variants()
                .variant("start", issuing(clock -> new pl.training.workshop.m4.s05_inlinevariable.start
                        .TicketIssuer(clock)::issue))
                .variant("step1", issuing(clock -> new pl.training.workshop.m4.s05_inlinevariable.step1
                        .TicketIssuer(clock)::issue))
                .variant("step2", issuing(clock -> new pl.training.workshop.m4.s05_inlinevariable.step2
                        .TicketIssuer(clock)::issue))
                .variant("step3", issuing(clock -> new pl.training.workshop.m4.s05_inlinevariable.step3
                        .TicketIssuer(clock)::issue))
                .expect("dwa bilety: kolejne numery, jeden odczyt zegara na bilet",
                        List.of(new Request("D1", 3), new Request("K2", 2)),
                        List.of(new Ticket("D1-1", "Bilet D1-1, cena 40.00, oplata 2.00",
                                        T0, T0.plusSeconds(15 * 60)),
                                new Ticket("K2-2", "Bilet K2-2, cena 32.00, oplata 2.00",
                                        T0.plusSeconds(1), T0.plusSeconds(1 + 15 * 60))))
                .expect("2D", List.of(new Request("A3", 1)),
                        List.of(new Ticket("A3-1", "Bilet A3-1, cena 25.00, oplata 2.00",
                                T0, T0.plusSeconds(15 * 60))))
                .tests();
    }

    static Function<List<Request>, List<Ticket>> issuing(
            Function<Clock, BiFunction<String, Integer, Ticket>> issuerFactory) {
        return requests -> {
            BiFunction<String, Integer, Ticket> issuer = issuerFactory.apply(new TickingClock(T0));
            List<Ticket> tickets = new ArrayList<>();
            for (Request request : requests) {
                tickets.add(issuer.apply(request.screeningCode(), request.format()));
            }
            return tickets;
        };
    }
}
