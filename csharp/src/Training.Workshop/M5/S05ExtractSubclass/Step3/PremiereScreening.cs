namespace Training.Workshop.M5.S05ExtractSubclass.Step3;

/// <summary>
/// Krok 3: Push Down - najpierw zachowanie (override Describe()), potem stan (pole _guest).
/// Gość jest wymagany w każdej premierze - null nie ma już gdzie się schować.
/// </summary>
public sealed class PremiereScreening : Screening
{
    private readonly string _guest;

    internal PremiereScreening(string title, string format, string guest) : base(title, format, true)
    {
        _guest = guest;
    }

    public override string Describe()
    {
        return base.Describe() + " - premiera, gość: " + _guest;
    }
}
