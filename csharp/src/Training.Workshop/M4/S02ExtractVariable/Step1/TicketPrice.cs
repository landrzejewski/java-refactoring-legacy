namespace Training.Workshop.M4.S02ExtractVariable.Step1;

/// <summary>
/// Krok 1: Extract Variable dla ceny bazowej, procentu zniżki i ceny po zniżce.
/// Każda nazwa odpowiada pojęciu z cennika, a nie fragmentowi składni.
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
        return discountedPrice
            - (r.Start < new TimeOnly(12, 0)
                ? 5.00m : 0m)
            + (r.Row != null && r.Row.Value >= 10 ? 10.00m : 0m)
            + (r.Format == 2 && !r.OwnGlasses ? 3.00m : 0m);
    }
}
