using System.Globalization;

namespace Training.Workshop.M4.S03MagicNumbers.Start;

/// <summary>
/// Start: podsumowanie zamówienia pełne magicznych liczb. Cztery różne "dziesiątki":
/// rząd VIP, dopłata VIP, próg grupy i kwota za punkt lojalnościowy. To cztery różne decyzje.
/// </summary>
public sealed class OrderPricer
{
    public string Summary(Order o)
    {
        decimal tickets = 0m;
        foreach (var t in o.Tickets)
        {
            decimal p = o.Format == 3 ? 40.00m
                : o.Format == 2 ? 32.00m : 25.00m;
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
                p = p - 5.00m;
            }
            if (t.Row >= 10)
            {
                p = p + 10.00m;
            }
            tickets = tickets + p;
        }
        if (o.Tickets.Count >= 10)
        {
            tickets = tickets * 0.90m;
        }
        tickets = Math.Round(tickets, 2, MidpointRounding.AwayFromZero);
        decimal fee = o.Online
            ? 2.00m * o.Tickets.Count
            : 0.00m;
        int points = (int)decimal.Truncate(tickets / 10);
        return string.Create(CultureInfo.InvariantCulture,
            $"Bilety: {tickets}, oplata: {fee}, razem: {tickets + fee}, punkty: {points}");
    }
}
