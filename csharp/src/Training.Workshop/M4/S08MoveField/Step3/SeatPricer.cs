using System.Globalization;

namespace Training.Workshop.M4.S08MoveField.Step3;

/// <summary>Krok 3: wycena pyta seans "czy VIP" jeden raz, zamiast porównywać surowy próg.</summary>
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
        bool vip = screening.IsVip(row);
        decimal price = vip ? @base + VipSurcharge : @base;
        return screening.Hall.Name + ", rzad " + row + (vip ? " (VIP)" : "") + ": "
            + price.ToString(CultureInfo.InvariantCulture);
    }
}
