namespace Training.Workshop.M7.S08DesignByContract.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): warunki końcowe (Ensure) i niezmiennik 0 &lt;= remaining &lt;= capacity.
/// Niezmiennik sprawdzamy dla nowej wartości PRZED przypisaniem - naruszenie nie zmienia obiektu.
/// Dla poprawnych wejść zachowanie identyczne jak w kroku 1; kontrole chronią przyszłe zmiany.
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
        CheckInvariant(_remaining);
    }

    public bool Reserve(int seats)
    {
        Contracts.Require(seats > 0, "seats must be positive");
        if (seats > _remaining)
        {
            return false;
        }
        var previous = _remaining;
        var next = previous - seats;
        CheckInvariant(next);
        _remaining = next;
        Contracts.Ensure(_remaining == previous - seats, "reserve must reduce remaining by seats");
        return true;
    }

    public void Release(int seats)
    {
        Contracts.Require(seats > 0, "seats must be positive");
        Contracts.Require(seats <= _capacity - _remaining, "cannot release more seats than reserved");
        var previous = _remaining;
        var next = previous + seats;
        CheckInvariant(next);
        _remaining = next;
        Contracts.Ensure(_remaining == previous + seats, "release must increase remaining by seats");
    }

    public int Remaining => _remaining;

    public int Capacity => _capacity;

    private void CheckInvariant(int candidate)
    {
        Contracts.Ensure(candidate >= 0 && candidate <= _capacity,
            "remaining must stay within 0.." + _capacity);
    }
}
