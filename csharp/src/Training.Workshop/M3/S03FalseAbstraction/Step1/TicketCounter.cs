namespace Training.Workshop.M3.S03FalseAbstraction.Step1;

/// <summary>
/// Krok 1: Inline Method (wszystkie wywołania, usuń Pricing). Kod wspólnej metody
/// wrócił do wywołującego razem z flagami - tymczasowo nieładnie, ale bezpiecznie.
/// </summary>
public sealed class TicketCounter
{
    public decimal Ticket(string format, bool morning, bool ownGlasses)
    {
        var pass = false;
        decimal unit;
        if (pass)
        {
            unit = 20.00m;
        }
        else
        {
            unit = format switch
            {
                "IMAX" => 40.00m,
                "3D" => 32.00m,
                _ => 25.00m,
            };
            if (morning)
            {
                unit -= 5.00m;
            }
        }
        if (format == "3D" && !ownGlasses && !pass)
        {
            unit += 3.00m;
        }
        return Math.Round(unit * 1, 2, MidpointRounding.AwayFromZero);
    }
}
