using System.Globalization;

namespace Training.Workshop.M7.S10BooleanParameter.Start;

/// <summary>
/// Start: dwie flagi w publicznym API. Wywołanie Book("Diuna", "IMAX", 2, true, false)
/// nie mówi, co znaczy true, a co false - trzeba zajrzeć do sygnatury.
/// </summary>
public sealed class TicketService
{
    private const decimal GlassesPrice = 3.00m;
    private const decimal Fee = 2.00m;

    public string Book(string title, string format, int seats, bool online, bool ownGlasses)
    {
        var basePrice = format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        decimal count = seats;
        var total = basePrice * count;
        if (format == "3D" && !ownGlasses)
        {
            total += GlassesPrice * count;
        }
        if (online)
        {
            total += Fee * count;
        }
        return title + " " + format + " x" + seats + (online ? " online" : " kasa") + ": "
            + total.ToString(CultureInfo.InvariantCulture);
    }
}
