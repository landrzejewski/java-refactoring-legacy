using Training.Workshop.Shared;

namespace Training.Workshop.M5.S05ExtractSubclass.Step4;

/// <summary>Krok 4: premiera = seans + gość + dopłata 15.00. Żadnego "if (_premiere)".</summary>
public sealed class PremiereScreening : Screening
{
    private static readonly Money PremiereSurcharge = Money.Of("15.00");

    private readonly string _guest;

    internal PremiereScreening(string title, string format, string guest) : base(title, format)
    {
        _guest = guest;
    }

    public override Money Price()
    {
        return base.Price().Plus(PremiereSurcharge);
    }

    public override string Describe()
    {
        return base.Describe() + " - premiera, gość: " + _guest;
    }
}
