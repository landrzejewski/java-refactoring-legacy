namespace Training.Workshop.M7.S08DesignByContract.Start;

/// <summary>
/// Start: pula miejsc seansu bez żadnych kontraktów. Reserve(-2) po cichu "dodaje" miejsca,
/// a Release() pozwala przekroczyć pojemność sali. Stan może stać się niemożliwy.
/// </summary>
public sealed class SeatPool
{
    private readonly int _capacity;
    private int _remaining;

    public SeatPool(int capacity)
    {
        _capacity = capacity;
        _remaining = capacity;
    }

    /// <summary>Zwraca false, gdy wolnych miejsc jest za mało - to poprawna, udokumentowana odpowiedź.</summary>
    public bool Reserve(int seats)
    {
        if (seats > _remaining)
        {
            return false;
        }
        _remaining = _remaining - seats;
        return true;
    }

    public void Release(int seats)
    {
        _remaining = _remaining + seats;
    }

    public int Remaining => _remaining;

    public int Capacity => _capacity;
}
