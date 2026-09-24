namespace Training.Workshop.M8.S10QualityGate.Start;

/// <summary>
/// Start: "bramka jakości" to lista kontrolna w wiki i dobra wola. Metoda Evaluate niczego
/// nie sprawdza, więc przepuszcza wszystko - fałszywe poczucie bezpieczeństwa.
/// <list type="bullet">
///   <item>testy zielone</item>
///   <item>brak ostrzeżeń kompilatora</item>
///   <item>brak TODO i Console.WriteLine w domenie</item>
///   <item>kluczowa klasa pokryta testem</item>
/// </list>
/// </summary>
public sealed class QualityGate
{
    /// <summary>Lista wyników bramki; pusta lista = bramka przepuszcza zmianę.</summary>
    public IReadOnlyList<string> Evaluate(GateInput input)
    {
        return [];
    }

    public bool Passes(GateInput input)
    {
        return Evaluate(input).Count == 0;
    }
}
