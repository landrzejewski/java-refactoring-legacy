package pl.training.workshop.m3.s12_cleanarchitecture;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Z zewnątrz (odpowiedź, zapisane wiersze, komunikaty) każdy krok zachowuje się identycznie. */
final class S12EquivalenceTest {
    record Request(Map<String, String> params, boolean dbAvailable) {
    }

    private static Function<Request, String> run(BiFunction<RowStore, Outbox, Function<Map<String, String>, String>> app) {
        return request -> {
            RowStore db = new RowStore(request.dbAvailable());
            Outbox outbox = new Outbox();
            String response = app.apply(db, outbox).apply(request.params());
            return response + " | db=" + db.dump() + " | outbox=" + outbox.messages();
        };
    }

    @TestFactory
    Stream<DynamicTest> everyStepHandlesRequestsTheSame() {
        return Scene.<Request, String>variants()
                .variant("start", run((db, outbox) -> pl.training.workshop.m3.s12_cleanarchitecture.start
                        .CinemaApplication.reservationController(db, outbox)::handle))
                .variant("step1", run((db, outbox) -> pl.training.workshop.m3.s12_cleanarchitecture.step1
                        .CinemaApplication.reservationController(db, outbox)::handle))
                .variant("step2", run((db, outbox) -> pl.training.workshop.m3.s12_cleanarchitecture.step2
                        .CinemaApplication.reservationController(db, outbox)::handle))
                .variant("step3", run((db, outbox) -> pl.training.workshop.m3.s12_cleanarchitecture.step3
                        .CinemaApplication.reservationController(db, outbox)::handle))
                .variant("step4", run((db, outbox) -> pl.training.workshop.m3.s12_cleanarchitecture.step4
                        .CinemaApplication.reservationController(db, outbox)::handle))
                .expect("IMAX, jedno miejsce VIP",
                        new Request(Map.of("email", "anna@kino.pl", "format", "IMAX", "rows", "5,10"), true),
                        "201 R-1 90.00 | db=[anna@kino.pl;IMAX;2;90.00]"
                                + " | outbox=[reservation-created:R-1;anna@kino.pl;90.00]")
                .expect("brak adresu e-mail", new Request(Map.of("format", "2D", "rows", "3"), true),
                        "400 brak email | db=[] | outbox=[]")
                .expect("brak miejsc", new Request(Map.of("email", "jan@kino.pl", "rows", ""), true),
                        "400 brak miejsc | db=[] | outbox=[]")
                .expect("baza niedostepna - brak powiadomienia",
                        new Request(Map.of("email", "jan@kino.pl", "format", "2D", "rows", "3"), false),
                        "503 baza niedostepna | db=[] | outbox=[]")
                .tests();
    }
}
