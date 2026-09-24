namespace Training.Workshop.M3.S03FalseAbstraction.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): karnet to osobna wiedza z osobnym właścicielem.
/// Nie wie nic o formatach, porankach ani okularach.
/// </summary>
public sealed class PassCounter
{
    private const decimal PricePerEntry = 20.00m;

    public decimal Pass(int entries)
    {
        return PricePerEntry * entries;
    }
}
