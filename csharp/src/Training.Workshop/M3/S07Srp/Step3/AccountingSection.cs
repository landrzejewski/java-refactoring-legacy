using System.Globalization;
using System.Text;

namespace Training.Workshop.M3.S07Srp.Step3;

/// <summary>Krok 3: aktor - księgowość. Zmienia się, gdy zmieniają się stawki VAT lub wymogi sprawozdań.</summary>
internal sealed class AccountingSection
{
    internal string Render(IReadOnlyList<Sale> sales)
    {
        var @out = new StringBuilder();
        var tickets = 0.00m;
        var bar = 0.00m;
        foreach (var sale in sales)
        {
            tickets += sale.TicketRevenue;
            bar += sale.BarRevenue;
        }
        @out.Append("KSIEGOWOSC\n");
        @out.Append(CultureInfo.InvariantCulture, $"Bilety brutto {tickets:0.00}, netto {Net(tickets, 8):0.00}\n");
        @out.Append(CultureInfo.InvariantCulture, $"Bar brutto {bar:0.00}, netto {Net(bar, 23):0.00}\n");
        var total = 0.00m;
        foreach (var sale in sales)
        {
            total += Revenue(sale);
        }
        @out.Append(CultureInfo.InvariantCulture, $"Razem brutto {total:0.00}\n");
        return @out.ToString();
    }

    private static decimal Revenue(Sale sale)
    {
        return sale.TicketRevenue + sale.BarRevenue;
    }

    private static decimal Net(decimal gross, int vatPercent)
    {
        return Math.Round(gross * 100 / (100 + vatPercent), 2, MidpointRounding.AwayFromZero);
    }
}
