namespace Training.Workshop.M3.S03FalseAbstraction.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Inline Variable dla stałych flag i Simplify - martwe gałęzie
/// karnetu znikają. Zostaje czysta reguła biletu: format, poranek, okulary 3D.
/// </summary>
public sealed class TicketCounter
{
    private const decimal MorningDiscount = 5.00m;
    private const decimal Glasses3D = 3.00m;

    public decimal Ticket(string format, bool morning, bool ownGlasses)
    {
        var price = format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        if (morning)
        {
            price -= MorningDiscount;
        }
        if (format == "3D" && !ownGlasses)
        {
            price += Glasses3D;
        }
        return price;
    }
}
