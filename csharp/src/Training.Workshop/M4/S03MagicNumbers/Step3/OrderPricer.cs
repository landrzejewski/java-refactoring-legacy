using System.Globalization;

namespace Training.Workshop.M4.S03MagicNumbers.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Constant dla stawek zniżek wg typu biletu.
/// Wszystkie liczby z cennika mają teraz nazwę i jednego właściciela;
/// pętla czyta się jak regulamin.
/// Stałe są prywatne - publiczne <c>const</c> kompilator wkleja do klientów (także w innych assembly).
/// </summary>
public sealed class OrderPricer
{
    private const decimal BasePrice2D = 25.00m;
    private const decimal BasePrice3D = 32.00m;
    private const decimal BasePriceImax = 40.00m;
    private const decimal MorningReduction = 5.00m;
    private const decimal VipSurcharge = 10.00m;
    private const decimal OnlineFeePerTicket = 2.00m;
    private const decimal StudentDiscount = 0.25m;
    private const decimal SeniorDiscount = 0.30m;
    private const decimal ChildDiscount = 0.40m;
    private const decimal NoFee = 0.00m;
    private const int MorningEndsAtHour = 12;
    private const int VipFromRow = 10;
    private const int GroupMinTickets = 10;
    private const decimal GroupPriceFactor = 0.90m;
    private const decimal AmountPerLoyaltyPoint = 10m;

    public string Summary(Order o)
    {
        decimal tickets = 0m;
        foreach (var t in o.Tickets)
        {
            decimal p = o.Format == 3 ? BasePriceImax
                : o.Format == 2 ? BasePrice3D : BasePrice2D;
            if (t.Type == "S")
            {
                p = p * (1 - StudentDiscount);
            }
            else if (t.Type == "E")
            {
                p = p * (1 - SeniorDiscount);
            }
            else if (t.Type == "C")
            {
                p = p * (1 - ChildDiscount);
            }
            if (o.Start.Hour < MorningEndsAtHour)
            {
                p = p - MorningReduction;
            }
            if (t.Row >= VipFromRow)
            {
                p = p + VipSurcharge;
            }
            tickets = tickets + p;
        }
        if (o.Tickets.Count >= GroupMinTickets)
        {
            tickets = tickets * GroupPriceFactor;
        }
        tickets = Math.Round(tickets, 2, MidpointRounding.AwayFromZero);
        decimal fee = o.Online
            ? OnlineFeePerTicket * o.Tickets.Count
            : NoFee;
        int points = (int)decimal.Truncate(tickets / AmountPerLoyaltyPoint);
        return string.Create(CultureInfo.InvariantCulture,
            $"Bilety: {tickets}, oplata: {fee}, razem: {tickets + fee}, punkty: {points}");
    }
}
