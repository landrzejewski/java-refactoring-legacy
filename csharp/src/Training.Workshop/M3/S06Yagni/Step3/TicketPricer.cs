namespace Training.Workshop.M3.S06Yagni.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Inline Class dla obu reguł i Safe Delete interfejsu
/// IPricingRule. Dwie aktualne reguły to dwa nazwane warunki - bez silnika.
/// <para>Czego YAGNI NIE zabrania i co tu zostaje: testów każdej reguły, nazwanych
/// stałych i metod (IsMorning, IsVip), szwu testowego (cena liczona z danych wejściowych,
/// bez zegara i statycznego stanu). Gdy pojawi się trzecia reguła z innym właścicielem
/// lub konfiguracja od biznesu, wydzielimy abstrakcję wtedy - w małych krokach, pod testami.</para>
/// </summary>
public sealed class TicketPricer
{
    private const decimal MorningDiscount = 5.00m;
    private const decimal VipSurcharge = 10.00m;

    public decimal Price(TicketQuote quote)
    {
        var price = BasePrice(quote.Format);
        if (IsMorning(quote))
        {
            price -= MorningDiscount;
        }
        if (IsVip(quote))
        {
            price += VipSurcharge;
        }
        return price;
    }

    private static bool IsMorning(TicketQuote quote)
    {
        return quote.Start.Hour < 12;
    }

    private static bool IsVip(TicketQuote quote)
    {
        return quote.Row >= quote.VipFromRow;
    }

    private static decimal BasePrice(string format)
    {
        return format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
    }
}
