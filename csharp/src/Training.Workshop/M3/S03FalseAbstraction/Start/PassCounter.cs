namespace Training.Workshop.M3.S03FalseAbstraction.Start;

/// <summary>Sprzedaż karnetu - format, poranek i okulary podane "na wszelki wypadek".</summary>
public sealed class PassCounter
{
    private readonly Pricing _pricing = new();

    public decimal Pass(int entries)
    {
        return _pricing.Price("2D", entries, true, false, true);
    }
}
