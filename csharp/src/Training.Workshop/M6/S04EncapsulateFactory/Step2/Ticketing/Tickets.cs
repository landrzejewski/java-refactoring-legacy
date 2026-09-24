using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step2.Ticketing;

/// <summary>Krok 2: Move Method - decyzja "który bilet" należy do fabryki, nie do klienta.</summary>
public static class Tickets
{
    private const int VipFromRow = 10;

    public static ITicket ForSeat(string title, Money basePrice, int row)
    {
        if (row >= VipFromRow)
        {
            return new VipTicket(title, basePrice, row);
        }
        return new StandardTicket(title, basePrice, row);
    }
}
