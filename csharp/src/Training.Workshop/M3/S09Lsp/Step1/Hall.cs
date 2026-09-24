namespace Training.Workshop.M3.S09Lsp.Step1;

/// <summary>
/// Krok 1: Hall implementuje ISeatMap. Sala kinowa z miejscami 1..Capacity.
/// <para>Kontrakt <see cref="Reserve"/>: dla wolnego miejsca z zakresu rezerwuje je - potem
/// <c>IsFree(seat) == false</c>, a <c>FreeSeats</c> maleje o 1. Zajęte miejsce:
/// <see cref="InvalidOperationException"/>. Nie ma warunku "ta sala może odmówić".</para>
/// </summary>
public class Hall : ISeatMap
{
    private readonly int _capacity;
    private readonly SortedSet<int> _taken = [];

    public Hall(int capacity)
    {
        _capacity = capacity;
    }

    protected Hall(int capacity, IReadOnlySet<int> alreadyTaken)
    {
        _capacity = capacity;
        _taken.UnionWith(alreadyTaken);
    }

    public virtual void Reserve(int seat)
    {
        if (seat < 1 || seat > _capacity)
        {
            throw new ArgumentException("brak miejsca " + seat);
        }
        if (!_taken.Add(seat))
        {
            throw new InvalidOperationException("miejsce zajete: " + seat);
        }
    }

    public bool IsFree(int seat)
    {
        return seat >= 1 && seat <= _capacity && !_taken.Contains(seat);
    }

    public int FreeSeats => _capacity - _taken.Count;

    public int Capacity => _capacity;
}
