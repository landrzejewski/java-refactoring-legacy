namespace Training.Workshop.M5.S04ExtractSuperclass.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public sealed class Screening : HallBooking
{
    private readonly string _title;

    public Screening(string title, string hall, DateTime start, int minutes) : base(hall, start, minutes)
    {
        _title = title;
    }

    public string Title => _title;
}
