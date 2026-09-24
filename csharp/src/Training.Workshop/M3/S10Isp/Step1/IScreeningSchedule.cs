namespace Training.Workshop.M3.S10Isp.Step1;

/// <summary>Krok 1: rola z perspektywy tablicy seansów.</summary>
public interface IScreeningSchedule
{
    void ScheduleScreening(string title, TimeOnly start);

    void CancelScreening(string title);

    IReadOnlyList<string> Screenings();
}
