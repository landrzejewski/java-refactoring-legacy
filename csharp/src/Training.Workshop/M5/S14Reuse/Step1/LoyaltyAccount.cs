using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Step1;

/// <summary>Krok 1: konto deleguje arytmetykę punktów do PointsLedger; publiczne API bez zmian.</summary>
public class LoyaltyAccount
{
    private readonly string _owner;
    private readonly PointsLedger _ledger = new();

    public LoyaltyAccount(string owner)
    {
        _owner = owner;
    }

    public void Earn(Money paidForTickets)
    {
        _ledger.Earn(paidForTickets);
    }

    public virtual bool RedeemFreeTicket()
    {
        return _ledger.Spend(100);
    }

    public string Owner => _owner;

    public int Points => _ledger.Points;
}
