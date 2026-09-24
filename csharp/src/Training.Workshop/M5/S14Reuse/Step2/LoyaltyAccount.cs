using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Step2;

/// <summary>Krok 2: konto klienta implementuje rolę IPointsHolder i nadal wymienia punkty na bilety.</summary>
public sealed class LoyaltyAccount : IPointsHolder
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

    public bool RedeemFreeTicket()
    {
        return _ledger.Spend(100);
    }

    public string Owner => _owner;

    public int Points => _ledger.Points;
}
