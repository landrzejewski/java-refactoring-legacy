package pl.training.workshop.m8.s13_livingdocs.start;

import java.util.List;

/**
 * Start: routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ROUTING.md pisany
 * ręcznie: raport przejęto miesiąc temu, anulowanie dodano tydzień temu, a dokument o tym nie wie.
 */
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
