namespace Training.Workshop.M7.S08DesignByContract.Step1;

/// <summary>
/// Krok 1: warunki wstępne (preconditions) przed pierwszą mutacją. To NIE jest refaktoryzacja:
/// dla niepoprawnych wejść zachowanie się zmienia (wyjątek zamiast cichego zepsucia stanu).
/// Dla poprawnych wejść - bez zmian, łącznie z "false" przy braku miejsc.
/// </summary>
public sealed class SeatPool
{
    private readonly int _capacity;
    private int _remaining;

    public SeatPool(int capacity)
    {
        Contracts.Require(capacity > 0, "capacity must be positive");
        _capacity = capacity;
        _remaining = capacity;
    }

    public bool Reserve(int seats)
    {
        Contracts.Require(seats > 0, "seats must be positive");
        if (seats > _remaining)
        {
            return false;
        }
        _remaining = _remaining - seats;
        return true;
    }

    public void Release(int seats)
    {
        Contracts.Require(seats > 0, "seats must be positive");
        Contracts.Require(seats <= _capacity - _remaining, "cannot release more seats than reserved");
        _remaining = _remaining + seats;
    }

    public int Remaining => _remaining;

    public int Capacity => _capacity;
}
