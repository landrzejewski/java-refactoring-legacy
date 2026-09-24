namespace Training.Workshop.M6.S06Builder.Step3;

/// <summary>Krok 3: wcięcia kodu odpowiadają poziomom drzewa dzień - sala - seans.</summary>
public sealed class WeekendPlanner
{
    public DaySchedule Plan(DateOnly date)
    {
        var dayOfWeek = date.DayOfWeek;
        var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
        return ScheduleBuilder.Day(date)
            .Hall("Sala 1", hall => hall
                .Screening("Diuna", 18, 0)
                .Screening("Diuna", 21, 0))
            .Hall("Sala 2", hall =>
            {
                if (weekend)
                {
                    hall.Screening("Kraina Lodu", 10, 0);
                }
                hall.Screening("Amator", 17, 30);
            })
            .Hall("Sala 3 VIP", hall =>
            {
                if (weekend)
                {
                    hall.Screening("Amator", 20, 0);
                }
            })
            .Build();
    }
}
