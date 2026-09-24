using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step2;

/// <summary>Krok 2: maraton - bez zmian względem kroku 1, poza konstruktorem bazy.</summary>
public sealed class MarathonScreening : Screening
{
    private readonly int _films;

    internal MarathonScreening(string title, int films)
        : base(title)
    {
        _films = films;
    }

    public override string Label()
    {
        return "Maraton: " + Title + " (" + _films + " filmy)";
    }

    public override int DurationMinutes()
    {
        return _films * 120 + (_films - 1) * 15;
    }

    public override Money Price()
    {
        return Money.Of("20.00").Times(_films);
    }
}
