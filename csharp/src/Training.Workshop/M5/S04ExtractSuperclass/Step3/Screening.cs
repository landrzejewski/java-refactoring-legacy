namespace Training.Workshop.M5.S04ExtractSuperclass.Step3;

/// <summary>Krok 3: Screening podaje tylko swoją nazwę do opisu konfliktu.</summary>
public sealed class Screening : HallBooking
{
    private readonly string _title;

    public Screening(string title, string hall, DateTime start, int minutes) : base(hall, start, minutes)
    {
        _title = title;
    }

    public string Title => _title;

    public override string Name => _title;
}
