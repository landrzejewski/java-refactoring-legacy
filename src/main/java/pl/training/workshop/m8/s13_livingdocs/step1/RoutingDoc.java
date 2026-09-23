package pl.training.workshop.m8.s13_livingdocs.step1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Krok 1: dokument żywy generowany z kodu. Tabela powstaje z Routing.routes(), więc nie może
 * rozjechać się z produkcją; test porównuje zapisany ROUTING.md z wygenerowanym.
 */
public final class RoutingDoc {
    private RoutingDoc() {
    }

    public static String render(List<Routing.Route> routes) {
        StringBuilder md = new StringBuilder("# Routing CineLegacy\n\n")
                .append("Plik generowany z Routing.routes() przez RoutingDoc - nie edytuj ręcznie.\n\n")
                .append("| Operacja | Obsługuje |\n")
                .append("| --- | --- |\n");
        for (Routing.Route route : routes) {
            md.append("| " + route.operation() + " | " + route.target() + " |").append('\n');
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
