using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step1;

/// <summary>Krok 1: pierwsza podklasa - maraton ma własne, nazwane dane (_films zamiast _value).</summary>
public sealed class MarathonScreening : Screening
{
    private readonly int _films;

    internal MarathonScreening(string title, int films)
        : base(Kind.Marathon, title, films)
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
