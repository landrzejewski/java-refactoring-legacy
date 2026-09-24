using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step2;

/// <summary>Krok 2: zwykły seans - 20 minut reklam przed filmem.</summary>
public sealed class RegularScreening : Screening
{
    private readonly int _runtime;

    internal RegularScreening(string title, int runtime)
        : base(title)
    {
        _runtime = runtime;
    }

    public override string Label()
    {
        return Title;
    }

    public override int DurationMinutes()
    {
        return 20 + _runtime;
    }

    public override Money Price()
    {
        return Money.Of("25.00");
    }
}
