using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step3;

/// <summary>Krok 3: zwykły seans jako rekord.</summary>
public sealed record RegularScreening(string Title, int Runtime) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return Title;
    }

    public override int DurationMinutes()
    {
        return 20 + Runtime;
    }

    public override Money Price()
    {
        return Money.Of("25.00");
    }
}
