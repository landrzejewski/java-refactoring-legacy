package pl.training.workshop.m8.s09_codemod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import com.sun.source.util.JavacTask;

/** Codemod na próbce kodu: co znajduje i jak przepisuje każda wersja; wynik musi się kompilować. */
final class S09SolutionTest {
    private static final String DESK = SampleProject.TICKET_DESK;

    private static final String MIGRATED = """
            package desk;

            import cinema.BookingService;
            import hotel.HotelService;
            import cinema.Channel;
            import cinema.Glasses;

            public class TicketDesk {
                private final BookingService bookings = new BookingService();
                private final HotelService hotels = new HotelService();

                public String online(String email, String[] seats, String[] types) {
                    return bookings.book("S1", email, seats, types, Channel.WEB, Glasses.RENTED);
                }

                public String boxOffice(String[] seats, String[] types, boolean own) {
                    // stary przyklad: bookings.book("S1", "x", seats, types, false, true)
                    return bookings.book("S2", "kasa@kino.pl",
                            seats, types,
                            Channel.BOX_OFFICE, own ? Glasses.OWN : Glasses.RENTED);
                }

                public String stay(String email, String[] rooms, String[] guests) {
                    return hotels.book("H1", email, rooms, guests, true, true);
                }
            }
            """;

    @Test
    void startRegexHitsCommentAndHotelButMissesMultilineCall() {
        var codemod = new pl.training.workshop.m8.s09_codemod.start.BookCallCodemod(SampleProject.API);
        assertEquals(List.of(11, 15, 22), codemod.findLines(DESK));
        String rewritten = codemod.rewrite(DESK);
        assertTrue(rewritten.contains("// stary przyklad: bookings.book(\"S1\", \"x\", seats, types, "
                + "Channel.BOX_OFFICE, Glasses.OWN)"), "regex przepisał komentarz");
        assertTrue(rewritten.contains("false, own);"), "wywołanie na kilku liniach zostało nietknięte");
    }

    @Test
    void step1AstSearchFindsRealCallsIncludingMultilineOne() {
        var codemod = new pl.training.workshop.m8.s09_codemod.step1.BookCallCodemod(SampleProject.API);
        assertEquals(List.of(11, 16, 22), codemod.findLines(DESK), "22 = HotelService: składnia nie zna typów");
    }

    @Test
    void step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent() {
        var codemod = new pl.training.workshop.m8.s09_codemod.step2.BookCallCodemod(SampleProject.API);
        String once = codemod.rewrite(DESK);
        assertTrue(once.contains("Channel.BOX_OFFICE, own ? Glasses.OWN : Glasses.RENTED);"));
        assertTrue(once.contains("hotels.book(\"H1\", email, rooms, guests, Channel.WEB, Glasses.OWN)"));
        assertFalse(problems(once).isEmpty(), "HotelService nie ma book(..., Channel, Glasses)");
        assertNotEquals(once, codemod.rewrite(once), "drugie uruchomienie psuje już zmigrowane wywołania");
    }

    @Test
    void step3TypeAwareRewriteMigratesOnlyTheDeprecatedApi() {
        var codemod = new pl.training.workshop.m8.s09_codemod.step3.BookCallCodemod(SampleProject.API);
        assertEquals(List.of(11, 16), codemod.findLines(DESK));
        assertEquals(MIGRATED, codemod.rewrite(DESK));
    }

    @Test
    void step3ResultCompilesWithoutWarningsAndSecondRunChangesNothing() {
        var codemod = new pl.training.workshop.m8.s09_codemod.step3.BookCallCodemod(SampleProject.API);
        assertEquals(List.of(), problems(MIGRATED));
        assertEquals(MIGRATED, codemod.rewrite(MIGRATED));
        assertEquals(List.of(), codemod.findLines(MIGRATED));
    }

    @Test
    void originalSampleCompilesButUsesDeprecatedApi() {
        assertEquals(List.of("WARNING compiler.warn.has.been.deprecated",
                "WARNING compiler.warn.has.been.deprecated"), problems(DESK));
    }

    /** Analiza (bez generowania klas) próbki razem z API, -Xlint:all; zwraca błędy i ostrzeżenia. */
    private static List<String> problems(String code) {
        List<JavaFileObject> files = new ArrayList<>();
        SampleProject.API.forEach(api -> files.add(file(api)));
        files.add(file(code));
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavacTask task = (JavacTask) ToolProvider.getSystemJavaCompiler()
                .getTask(null, null, diagnostics, List.of("-proc:none", "-Xlint:all"), null, files);
        try {
            task.analyze();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        List<String> problems = new ArrayList<>();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.NOTE) {
                String kind = d.getKind() == Diagnostic.Kind.ERROR ? "ERROR" : "WARNING";
                problems.add(kind + " " + d.getCode());
            }
        }
        return problems;
    }

    private static JavaFileObject file(String source) {
        Matcher name = Pattern.compile("public\\s+(?:class|enum|interface)\\s+(\\w+)").matcher(source);
        String fileName = (name.find() ? name.group(1) : "Source") + ".java";
        return new SimpleJavaFileObject(URI.create("string:///" + fileName), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
    }
}
