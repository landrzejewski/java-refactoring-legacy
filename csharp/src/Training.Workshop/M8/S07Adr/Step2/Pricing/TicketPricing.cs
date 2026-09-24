using Training.Workshop.Shared;

namespace Training.Workshop.M8.S07Adr.Step2.Pricing;

/// <summary>Krok 2: spełnienie R2 - Money zamiast liczby zmiennoprzecinkowej. Moduł zgodny z ADR-0007.</summary>
public sealed class TicketPricing
{
    private const int GroupSize = 10;
    private const int GroupDiscountPercent = 10;

    public Quote Total(int tickets, Money unitPrice)
    {
        Money sum = unitPrice.Times(tickets);
        bool groupDiscount = tickets >= GroupSize;
        if (groupDiscount)
        {
            sum = sum.Minus(sum.Percent(GroupDiscountPercent));
        }
        return new Quote(sum, groupDiscount);
    }
}
