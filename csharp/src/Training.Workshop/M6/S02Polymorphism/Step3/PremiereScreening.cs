using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step3;

/// <summary>Krok 3: premiera jako rekord.</summary>
public sealed record PremiereScreening(string Title, int Runtime) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return "Premiera: " + Title;
    }

    public override int DurationMinutes()
    {
        return 30 + Runtime;
    }

    public override Money Price()
    {
        return Money.Of("35.00");
    }
}
