namespace Training.Workshop.M3.S10Isp.Start;

/// <summary>Klient: tablica seansów. Używa ScheduleScreening, CancelScreening i Screenings.</summary>
public sealed class ScheduleBoard
{
    private readonly ICinemaAdminService _backOffice;

    public ScheduleBoard(ICinemaAdminService backOffice)
    {
        _backOffice = backOffice;
    }

    public void Plan(string title, TimeOnly start)
    {
        _backOffice.ScheduleScreening(title, start);
    }

    public void Cancel(string title)
    {
        _backOffice.CancelScreening(title);
    }

    public string Board()
    {
        return string.Join(", ", _backOffice.Screenings());
    }
}
