namespace Training.Workshop.M4.S02ExtractVariable.Step2;

/// <summary>
/// Krok 2: Extract Variable dla warunków - morning, vipSeat, needsGlasses.
/// vipSeat wydzielamy RAZEM z osłoną <c>r.Row != null &amp;&amp;</c>: krótkie spięcie jest zachowaniem.
/// </summary>
public sealed class TicketPrice
{
    public decimal Price(TicketRequest r)
    {
        decimal basePrice = r.Format == 3 ? 40.00m
            : r.Format == 2 ? 32.00m : 25.00m;
        int discountPercent = r.Type == "S" ? 25
            : r.Type == "E" ? 30 : r.Type == "C" ? 40 : 0;
        decimal discountedPrice = Math.Round(basePrice * (100 - discountPercent) / 100,
            2, MidpointRounding.AwayFromZero);
        bool morning = r.Start < new TimeOnly(12, 0);
        bool vipSeat = r.Row != null && r.Row.Value >= 10;
        bool needsGlasses = r.Format == 2 && !r.OwnGlasses;
        return discountedPrice
            - (morning ? 5.00m : 0m)
            + (vipSeat ? 10.00m : 0m)
            + (needsGlasses ? 3.00m : 0m);
    }
}
