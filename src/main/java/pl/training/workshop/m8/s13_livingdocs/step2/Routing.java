package pl.training.workshop.m8.s13_livingdocs.step2;

import java.util.List;

/**
 * Krok 2: każda trasa ma właściciela i kryterium usunięcia - element przejściowy bez nich
 * staje się nowym legacy. Routing (zachowanie) bez zmian.
 */
public final class Routing {
    public record Route(String operation, String target, String owner, String removeWhen) {
    }

    private Routing() {
    }

    public static List<Route> routes() {
        return List.of(
                new Route("book", "new", "zespol Sprzedaz", "-"),
                new Route("report", "new", "zespol Raporty", "-"),
                new Route("cancel", "legacy", "zespol Sprzedaz",
                        "CancelModule w trybie CANDIDATE przez 14 dni"));
    }
}
