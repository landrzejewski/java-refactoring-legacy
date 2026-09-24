namespace Training.Workshop.M6.S07Decorator.Step2;

/// <summary>Krok 2: fabryka składa łańcuch - rdzeń, a na zewnątrz opcjonalne ubezpieczenie.</summary>
public sealed class TicketAssembler
{
    public IPricedTicket Assemble(TicketOrder order)
    {
        IPricedTicket ticket = new Ticket(order);
        if (order.Insurance)
        {
            ticket = new Insurance(ticket);
        }
        return ticket;
    }
}
