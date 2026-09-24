using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Step2;

/// <summary>Krok 2: bez zmian - teraz używają jej dwie niezależne klasy.</summary>
public sealed class PointsLedger
{
    private int _points;

    public void Earn(Money paidForTickets)
    {
        _points += (int)decimal.Truncate(paidForTickets.Amount / 10);
    }

    public bool Spend(int amount)
    {
        if (_points < amount)
        {
            return false;
        }
        _points -= amount;
        return true;
    }

    public int Points => _points;
}
