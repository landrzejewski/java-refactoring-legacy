using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step1.Ticketing;

/// <summary>Krok 1: metody tworzące (Creation Method) obok klas biletów - zwracają typ ITicket.</summary>
public static class Tickets
{
    public static ITicket Standard(string title, Money basePrice, int row)
    {
        return new StandardTicket(title, basePrice, row);
    }

    public static ITicket Vip(string title, Money basePrice, int row)
    {
        return new VipTicket(title, basePrice, row);
    }
}
