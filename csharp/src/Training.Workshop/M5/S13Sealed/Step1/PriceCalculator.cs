using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step1;

/// <summary>Krok 1: bez zmian - zamknięcie hierarchii jeszcze nic tu nie wymusza, bo łańcuch if nie jest wyczerpujący.</summary>
public sealed class PriceCalculator
{
    public int DiscountPercent(Ticket ticket)
    {
        if (ticket is StudentTicket)
        {
            return 25;
        }
        else if (ticket is SeniorTicket)
        {
            return 30;
        }
        return 0;
    }

    public Money Price(Ticket ticket)
    {
        return ticket.BasePrice.Minus(ticket.BasePrice.Percent(DiscountPercent(ticket)));
    }
}
