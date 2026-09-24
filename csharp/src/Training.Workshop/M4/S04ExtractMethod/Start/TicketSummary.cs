using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S04ExtractMethod.Start;

public sealed class TicketSummary
{
    public string Describe(Order order)
    {
        // cena bazowa formatu
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

        // suma i liczba miejsc VIP
        decimal subtotal = 0.00m;
        int vipSeats = 0;
        foreach (var row in order.Rows)
        {
            decimal price = @base;
            if (row >= 10)
            {
                price = price + 10.00m;
                vipSeats++;
            }
            subtotal = subtotal + price;
        }
        subtotal = Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);

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
}
