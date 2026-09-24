namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>
/// Krok 3: kolejność owijania jest kontraktem - odtwarza kolejność dodatków w opisie:
/// rdzeń, VIP, okulary, ubezpieczenie.
/// </summary>
public sealed class TicketAssembler
{
    public IPricedTicket Assemble(TicketOrder order)
    {
        IPricedTicket ticket = new Ticket(order.Title, order.Format, order.Base);
        if (order.Vip)
        {
            ticket = new VipSeat(ticket);
        }
        if (order.Format == "3D" && !order.OwnGlasses)
        {
            ticket = new Glasses3D(ticket);
        }
        if (order.Insurance)
        {
            ticket = new Insurance(ticket);
        }
        return ticket;
    }
}
