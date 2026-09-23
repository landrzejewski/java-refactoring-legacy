package pl.training.workshop.m8.s13_livingdocs.step2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Krok 2: dokument pokazuje też architekturę przejściową - właściciela i kryterium usunięcia
 * każdej trasy. Informacja operacyjna jest żywa, uzasadnienie decyzji zostaje w ADR (historia).
 */
public final class RoutingDoc {
    private RoutingDoc() {
    }

    public static String render(List<Routing.Route> routes) {
        StringBuilder md = new StringBuilder("# Routing CineLegacy\n\n")
                .append("Plik generowany z Routing.routes() przez RoutingDoc - nie edytuj ręcznie.\n\n")
                .append("| Operacja | Obsługuje | Właściciel | Usunąć, gdy |\n")
                .append("| --- | --- | --- | --- |\n");
        for (Routing.Route route : routes) {
            md.append("| " + route.operation() + " | " + route.target() + " | " + route.owner()
                    + " | " + route.removeWhen() + " |").append('\n');
        }
        return md.toString();
    }

    /** Ścieżka dokumentu obok kodu - liczona z pakietu, więc działa też po "jump" do start. */
    public static Path location() {
        return Path.of("src/main/java", RoutingDoc.class.getPackageName().replace('.', '/'), "ROUTING.md");
    }

    /** Regeneracja dokumentu - uruchom z katalogu głównego repozytorium. */
    public static void main(String[] args) throws IOException {
        Files.writeString(location(), render(Routing.routes()));
    }
}
