namespace Training.Workshop.M5.S04ExtractSuperclass.Step1;

/// <summary>
/// Krok 1: Extract Superclass z klasy Screening. Nazwa opisuje pojęcie domenowe (rezerwacja sali),
/// a nie motywację ("BaseScreening", "AbstractCommon"). Pola prywatne, ustawiane przez base(...).
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
}
