using System.Globalization;

namespace Training.Workshop.M4.S03MagicNumbers.Step1;

/// <summary>
/// Krok 1: Extract Constant dla kwot z cennika (ceny bazowe, poranek, VIP, opłata online).
/// <c>private const decimal</c> - jeden właściciel, wartość niezmienna i nazwana w jednym miejscu.
/// </summary>
public sealed class OrderPricer
{
    private const decimal BasePrice2D = 25.00m;
    private const decimal BasePrice3D = 32.00m;
    private const decimal BasePriceImax = 40.00m;
    private const decimal MorningReduction = 5.00m;
    private const decimal VipSurcharge = 10.00m;
    private const decimal OnlineFeePerTicket = 2.00m;
    private const decimal NoFee = 0.00m;

    public string Summary(Order o)
    {
        decimal tickets = 0m;
        foreach (var t in o.Tickets)
        {
            decimal p = o.Format == 3 ? BasePriceImax
                : o.Format == 2 ? BasePrice3D : BasePrice2D;
            if (t.Type == "S")
            {
                p = p * (1 - 0.25m);
            }
            else if (t.Type == "E")
            {
                p = p * (1 - 0.30m);
            }
            else if (t.Type == "C")
            {
                p = p * (1 - 0.40m);
            }
            if (o.Start.Hour < 12)
            {
                p = p - MorningReduction;
            }
            if (t.Row >= 10)
            {
                p = p + VipSurcharge;
            }
            tickets = tickets + p;
        }
        if (o.Tickets.Count >= 10)
        {
            tickets = tickets * 0.90m;
        }
        tickets = Math.Round(tickets, 2, MidpointRounding.AwayFromZero);
        decimal fee = o.Online
            ? OnlineFeePerTicket * o.Tickets.Count
            : NoFee;
        int points = (int)decimal.Truncate(tickets / 10);
        return string.Create(CultureInfo.InvariantCulture,
            $"Bilety: {tickets}, oplata: {fee}, razem: {tickets + fee}, punkty: {points}");
    }
}
