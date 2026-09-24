namespace Training.Workshop.M3.S08Ocp.Step2;

/// <summary>
/// Krok 2: ScreeningOffer jest zamknięta na oś "format seansu" - nie zmieni się,
/// gdy dojdzie format. Nie jest zamknięta na inne osie (np. nowa dopłata za fotel
/// premium) i nie musi: zamykamy tylko oś, która faktycznie się zmienia.
/// </summary>
public sealed class ScreeningOffer
{
    private const decimal Glasses = 3.00m;

    public decimal Price(string code, bool ownGlasses)
    {
        var format = Format.Parse(code);
        var glasses = format.NeedsGlasses && !ownGlasses ? Glasses : 0m;
        return format.BasePrice + glasses;
    }

    public string Label(string code)
    {
        return Format.Parse(code).Label;
    }
}
