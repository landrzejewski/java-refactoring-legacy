namespace Training.Workshop.M6.S07Decorator.Start;

/// <summary>Start: jedyne miejsce tworzenia biletu - tu później złożymy łańcuch dekoratorów.</summary>
public sealed class TicketAssembler
{
    public Ticket Assemble(TicketOrder order)
    {
        return new Ticket(order);
    }
}
