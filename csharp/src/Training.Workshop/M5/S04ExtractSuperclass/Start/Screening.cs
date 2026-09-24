namespace Training.Workshop.M5.S04ExtractSuperclass.Start;

/// <summary>Start: seans - sala, początek, czas trwania i koniec. PrivateEvent ma to samo, osobno.</summary>
public sealed class Screening
{
    private readonly string _title;
    private readonly string _hall;
    private readonly DateTime _start;
    private readonly int _minutes;

    public Screening(string title, string hall, DateTime start, int minutes)
    {
        _title = title;
        _hall = hall;
        _start = start;
        _minutes = minutes;
    }

    public string Title => _title;

    public string Hall => _hall;

    public DateTime Start => _start;

    public DateTime End => _start.AddMinutes(_minutes);
}
