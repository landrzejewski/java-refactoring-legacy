using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step3;

/// <summary>Krok 3: maraton jako rekord - pole Films ma jedno znaczenie.</summary>
public sealed record MarathonScreening(string Title, int Films) : Screening
{
    public override string Title { get; } = Title;

    public override string Label()
    {
        return "Maraton: " + Title + " (" + Films + " filmy)";
    }

    public override int DurationMinutes()
    {
        return Films * 120 + (Films - 1) * 15;
    }

    public override Money Price()
    {
        return Money.Of("20.00").Times(Films);
    }
}
