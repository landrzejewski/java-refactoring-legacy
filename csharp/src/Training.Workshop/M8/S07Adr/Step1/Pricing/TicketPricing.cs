namespace Training.Workshop.M8.S07Adr.Step1.Pricing;

/// <summary>
/// Krok 1: spełnienie R1 - cennik nie zna powiadomień. Zwraca Quote z flagą rabatu,
/// a decyzję o mailu podejmuje BookingService. R2 (double) nadal naruszona.
/// </summary>
public sealed class TicketPricing
{
    public Quote Total(int tickets, double unitPrice)
    {
        double sum = unitPrice * tickets;
        bool groupDiscount = tickets >= 10;
        if (groupDiscount)
        {
            sum = sum - sum * 0.10;
        }
        return new Quote(Math.Floor(sum * 100 + 0.5) / 100.0, groupDiscount);
    }
}
