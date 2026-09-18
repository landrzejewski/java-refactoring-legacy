using Training.Module8.Documentation;

namespace Training.Module8.Tests.Documentation;

public sealed class DecisionRecordMarkdownRendererTest
{
    private readonly DecisionRecordMarkdownRenderer renderer = new();

    [Fact]
    public void RendersEveryFieldInDeterministicOrder()
    {
        var record = new DecisionRecord(
            new DecisionId("ADR-0042"),
            "Wyodrębnij port zegara",
            DecisionStatus.Accepted,
            "Logika domenowa odczytuje czas systemowy bezpośrednio.",
            "Wprowadzamy port zegara na granicy aplikacji.",
            [
                new DecisionOption(
                    "Port zegara",
                    "Pozwala kontrolować czas w testach."),
                new DecisionOption(
                    "Mockowanie statyczne",
                    "Nie zmienia sygnatur, ale zwiększa sprzężenie testów.")
            ],
            [
                new DecisionConsequence(
                    ConsequenceKind.Positive,
                    "Testy stają się deterministyczne."),
                new DecisionConsequence(
                    ConsequenceKind.Negative,
                    "Konstruktor otrzymuje dodatkową zależność.")
            ],
            "Uruchom testy charakteryzujące przed i po zmianie.");

        // Surowy literał C# nie zawiera końcowego znaku nowej linii
        // (w przeciwieństwie do text block z Javy), stąd jawne "\n".
        string expected = """
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

            """.ReplaceLineEndings("\n") + "\n";

        Assert.Equal(expected, renderer.Render(record));
        Assert.Equal(expected, renderer.Render(record));
    }

    [Fact]
    public void RejectsANullRecord()
    {
        Assert.Throws<ArgumentNullException>(() => renderer.Render(null!));
    }
}
