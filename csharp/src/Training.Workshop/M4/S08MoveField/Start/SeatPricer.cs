using System.Globalization;

namespace Training.Workshop.M4.S08MoveField.Start;

/// <summary>Wycena miejsca na seansie.</summary>
public sealed class SeatPricer
{
    private const decimal VipSurcharge = 10.00m;

    public string Quote(Screening screening, int row)
    {
        decimal @base = screening.Format switch
        {
            3 => 40.00m,
            2 => 32.00m,
            _ => 25.00m,
        };
        decimal price = row >= screening.VipFromRow ? @base + VipSurcharge : @base;
        return screening.Hall.Name + ", rzad " + row
            + (screening.IsVip(row) ? " (VIP)" : "") + ": " + price.ToString(CultureInfo.InvariantCulture);
    }
}
