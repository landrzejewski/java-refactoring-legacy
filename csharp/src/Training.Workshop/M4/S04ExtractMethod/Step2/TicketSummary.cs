using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S04ExtractMethod.Step2;

/// <summary>
/// Krok 2: pętla miała DWA wyjścia (subtotal i vipSeats), więc IDE
/// nie wydzieli jej w jedną metodę. Najpierw Split Loop, potem dwa
/// razy Extract Method - każda metoda ma jedno wyjście.
/// </summary>
public sealed class TicketSummary
{
    public string Describe(Order order)
    {
        decimal @base = BasePrice(order);
        decimal subtotal = Subtotal(order, @base);
        int vipSeats = VipSeats(order);

        // dokument
        var text = new StringBuilder();
        text.Append("BILETY: ").Append(order.Title).Append('\n');
        text.Append("Format: ").Append(order.Format)
            .Append(", start ").Append(order.Start.ToString("HH:mm", CultureInfo.InvariantCulture)).Append('\n');
        text.Append("Miejsc: ").Append(order.Rows.Count);
        if (vipSeats > 0)
        {
            text.Append(" (w tym VIP: ").Append(vipSeats).Append(')');
        }
        text.Append('\n');
        text.Append("Razem: ").Append(subtotal.ToString(CultureInfo.InvariantCulture)).Append('\n');
        return text.ToString();
    }

    private decimal BasePrice(Order order)
    {
        decimal @base;
        if (order.Format == "IMAX")
        {
            @base = 40.00m;
        }
        else if (order.Format == "3D")
        {
            @base = 32.00m;
        }
        else
        {
            @base = 25.00m;
        }
        if (order.Start.Hour < 12)
        {
            @base = @base - 5.00m;
        }
        return @base;
    }

    private decimal Subtotal(Order order, decimal @base)
    {
        decimal subtotal = 0.00m;
        foreach (var row in order.Rows)
        {
            decimal price = @base;
            if (row >= 10)
            {
                price = price + 10.00m;
            }
            subtotal = subtotal + price;
        }
        return Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);
    }

    private int VipSeats(Order order)
    {
        int vipSeats = 0;
        foreach (var row in order.Rows)
        {
            if (row >= 10)
            {
                vipSeats++;
            }
        }
        return vipSeats;
    }
}
