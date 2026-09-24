using Training.Workshop.Shared;

namespace Training.Workshop.M5.S14Reuse.Step1;

/// <summary>
/// Krok 1: Extract Delegate - współdzielona IMPLEMENTACJA (naliczanie i wydawanie punktów)
/// dostaje własną klasę. Reużycie kodu nie wymaga już dziedziczenia.
/// </summary>
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
