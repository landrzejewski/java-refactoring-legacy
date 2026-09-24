namespace Training.Workshop.M6.S06Builder.Step1;

/// <summary>Krok 1: klient mówi "co" zbudować; kształt kodu przypomina kształt repertuaru.</summary>
public sealed class WeekendPlanner
{
    public DaySchedule Plan(DateOnly date)
    {
        var dayOfWeek = date.DayOfWeek;
        var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
        var builder = new ScheduleBuilder(date)
            .Hall("Sala 1")
            .Screening("Diuna", 18, 0)
            .Screening("Diuna", 21, 0)
            .Hall("Sala 2");
        if (weekend)
        {
            builder.Screening("Kraina Lodu", 10, 0);
        }
        builder.Screening("Amator", 17, 30)
            .Hall("Sala 3 VIP");
        if (weekend)
        {
            builder.Screening("Amator", 20, 0);
        }
        return builder.Build();
    }
}
