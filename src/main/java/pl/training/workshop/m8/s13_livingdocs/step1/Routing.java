package pl.training.workshop.m8.s13_livingdocs.step1;

import java.util.List;

/** Krok 1 (bez zmian): routing jako kod - jedyne źródło prawdy dla dokumentu. */
public final class Routing {
    public record Route(String operation, String target) {
    }

    private Routing() {
    }

    public static List<Route> routes() {
        return List.of(
                new Route("book", "new"),
                new Route("report", "new"),
                new Route("cancel", "legacy"));
    }
}
