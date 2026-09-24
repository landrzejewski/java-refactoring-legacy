namespace Training.Workshop.M5.S04ExtractSuperclass.Step2;

/// <summary>Krok 2: bez zmian - nadklasa jest gotowa na drugiego potomka.</summary>
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
