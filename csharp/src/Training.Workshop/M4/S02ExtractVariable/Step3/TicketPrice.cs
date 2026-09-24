namespace Training.Workshop.M4.S02ExtractVariable.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Variable dla kwot dopłat i obniżek.
/// Ostatnia instrukcja czyta się jak paragon: cena po zniżce - poranek + VIP + okulary.
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
        decimal morningReduction = morning ? 5.00m : 0m;
        decimal vipSurcharge = vipSeat ? 10.00m : 0m;
        decimal glassesFee = needsGlasses ? 3.00m : 0m;
        return discountedPrice - morningReduction + vipSurcharge + glassesFee;
    }
}
