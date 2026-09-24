using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step2;

/// <summary>Krok 2: premiera - 30 minut spotkania z twórcami zamiast reklam.</summary>
public sealed class PremiereScreening : Screening
{
    private readonly int _runtime;

    internal PremiereScreening(string title, int runtime)
        : base(title)
    {
        _runtime = runtime;
    }

    public override string Label()
    {
        return "Premiera: " + Title;
    }

    public override int DurationMinutes()
    {
        return 30 + _runtime;
    }

    public override Money Price()
    {
        return Money.Of("35.00");
    }
}
