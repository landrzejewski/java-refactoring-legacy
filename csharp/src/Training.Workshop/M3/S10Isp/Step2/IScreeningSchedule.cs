namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Rola z perspektywy tablicy seansów.</summary>
public interface IScreeningSchedule
{
    void ScheduleScreening(string title, TimeOnly start);

    void CancelScreening(string title);

    IReadOnlyList<string> Screenings();
}
