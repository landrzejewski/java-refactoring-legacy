using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Start;

/// <summary>Start: konto lojalnościowe klienta - 1 pkt za pełne 10.00, 100 pkt = darmowy bilet 2D.</summary>
public class LoyaltyAccount
{
    private readonly string _owner;
    private int _points;

    public LoyaltyAccount(string owner)
    {
        _owner = owner;
    }

    public void Earn(Money paidForTickets)
    {
        _points += (int)decimal.Truncate(paidForTickets.Amount / 10);
    }

    public virtual bool RedeemFreeTicket()
    {
        if (_points < 100)
        {
            return false;
        }
        _points -= 100;
        return true;
    }

    public string Owner => _owner;

    public int Points => _points;
}
