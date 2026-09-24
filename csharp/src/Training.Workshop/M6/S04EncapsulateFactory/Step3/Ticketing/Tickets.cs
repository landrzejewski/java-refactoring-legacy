using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing;

/// <summary>
/// Krok 3: fabryka jest jedynym publicznym wejściem do tworzenia biletów. Klasy konkretne
/// są prywatnymi typami zagnieżdżonymi (klasa partial - każdy bilet nadal we własnym pliku).
/// </summary>
public static partial class Tickets
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
