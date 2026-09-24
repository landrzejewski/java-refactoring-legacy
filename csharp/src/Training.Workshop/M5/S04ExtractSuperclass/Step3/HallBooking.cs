namespace Training.Workshop.M5.S04ExtractSuperclass.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): warunek kolizji wyciągnięty (Extract Method) i przeniesiony do nadklasy
/// jako <c>Overlaps</c>; <c>Name</c> to abstrakcyjny punkt rozszerzenia dla opisu konfliktu.
/// </summary>
public abstract class HallBooking
{
    private readonly string _hall;
    private readonly DateTime _start;
    private readonly int _minutes;

    protected HallBooking(string hall, DateTime start, int minutes)
    {
        _hall = hall;
        _start = start;
        _minutes = minutes;
    }

    public string Hall => _hall;

    public DateTime Start => _start;

    public DateTime End => _start.AddMinutes(_minutes);

    public abstract string Name { get; }

    public bool Overlaps(HallBooking other)
    {
        return _hall == other._hall && _start < other.End && other._start < End;
    }
}
