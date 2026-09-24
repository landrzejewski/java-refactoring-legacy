namespace Training.Workshop.M3.S09Lsp.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Replace Inheritance with Delegation. Sala archiwalna
/// implementuje tylko rolę, której kontrakt spełnia - <see cref="ISeatMap"/> - i deleguje
/// do prywatnej kopii Hall. Metody Reserve po prostu nie ma: kompilator nie pozwoli
/// przekazać sali archiwalnej do kasy.
/// </summary>
public sealed class ReadOnlyHall : ISeatMap
{
    private readonly Hall _snapshot;

    public ReadOnlyHall(int capacity, IReadOnlySet<int> taken)
    {
        _snapshot = new Hall(capacity);
        foreach (var seat in taken)
        {
            _snapshot.Reserve(seat);
        }
    }

    public bool IsFree(int seat)
    {
        return _snapshot.IsFree(seat);
    }

    public int FreeSeats => _snapshot.FreeSeats;

    public int Capacity => _snapshot.Capacity;
}
