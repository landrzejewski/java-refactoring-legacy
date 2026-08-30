package pl.training.module8.documentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

final class DecisionRecordMarkdownRendererTest {
    private final DecisionRecordMarkdownRenderer renderer =
            new DecisionRecordMarkdownRenderer();

    @Test
    void rendersEveryFieldInDeterministicOrder() {
        var record = new DecisionRecord(
                new DecisionId("ADR-0042"),
                "Wyodrębnij port zegara",
                DecisionStatus.ACCEPTED,
                "Logika domenowa odczytuje czas systemowy bezpośrednio.",
                "Wprowadzamy port zegara na granicy aplikacji.",
                List.of(
                        new DecisionOption(
                                "Port zegara",
                                "Pozwala kontrolować czas w testach."),
                        new DecisionOption(
                                "Mockowanie statyczne",
                                "Nie zmienia sygnatur, ale zwiększa sprzężenie testów.")),
                List.of(
                        new DecisionConsequence(
                                ConsequenceKind.POSITIVE,
                                "Testy stają się deterministyczne."),
                        new DecisionConsequence(
                                ConsequenceKind.NEGATIVE,
                                "Konstruktor otrzymuje dodatkową zależność.")),
                "Uruchom testy charakteryzujące przed i po zmianie.");

        String expected = """
                # ADR-0042: Wyodrębnij port zegara

                ## Status

                Zaakceptowana

                ## Kontekst

                Logika domenowa odczytuje czas systemowy bezpośrednio.

                ## Decyzja

                Wprowadzamy port zegara na granicy aplikacji.

                ## Rozważone opcje

                1. **Port zegara**: Pozwala kontrolować czas w testach.
                2. **Mockowanie statyczne**: Nie zmienia sygnatur, ale zwiększa sprzężenie testów.

                ## Konsekwencje

                - **Pozytywna**: Testy stają się deterministyczne.
                - **Negatywna**: Konstruktor otrzymuje dodatkową zależność.

                ## Metoda weryfikacji

                Uruchom testy charakteryzujące przed i po zmianie.

                """;

        assertEquals(expected, renderer.render(record));
        assertEquals(expected, renderer.render(record));
    }

    @Test
    void rejectsANullRecord() {
        assertThrows(NullPointerException.class, () -> renderer.render(null));
    }
}
