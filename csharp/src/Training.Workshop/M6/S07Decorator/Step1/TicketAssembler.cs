namespace Training.Workshop.M6.S07Decorator.Step1;

/// <summary>Krok 1: fabryka zwraca interfejs, więc może później zwrócić udekorowany obiekt.</summary>
public sealed class TicketAssembler
{
    public IPricedTicket Assemble(TicketOrder order)
    {
        return new Ticket(order);
    }
}
