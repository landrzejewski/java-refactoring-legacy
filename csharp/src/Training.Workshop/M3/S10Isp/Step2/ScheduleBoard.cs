namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Tablica zależy tylko od roli IScreeningSchedule.</summary>
public sealed class ScheduleBoard
{
    private readonly IScreeningSchedule _backOffice;

    public ScheduleBoard(IScreeningSchedule backOffice)
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
