using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Replace Inheritance with Delegation - konto firmowe ma własny PointsLedger
/// i NIE jest LoyaltyAccount. Nie ma czego blokować wyjątkiem: RedeemFreeTicket() po prostu nie istnieje.
/// </summary>
public sealed class CorporateAccount : IPointsHolder
{
    private readonly string _company;
    private readonly PointsLedger _ledger = new();

    public CorporateAccount(string company)
    {
        _company = company;
    }

    public void Earn(Money paidForTickets)
    {
        _ledger.Earn(paidForTickets);
    }

    public string Owner => _company;

    public int Points => _ledger.Points;
}
