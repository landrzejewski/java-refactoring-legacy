namespace Training.Workshop.M6.S06Builder.Start;

/// <summary>
/// Start: klient buduje drzewo dzień - sala - seans ręcznie. Dużo new/Add, łatwo zapomnieć
/// day.Add(hall), a kod nie przypomina kształtu repertuaru.
/// </summary>
public sealed class WeekendPlanner
{
    public DaySchedule Plan(DateOnly date)
    {
        var dayOfWeek = date.DayOfWeek;
        var weekend = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday;
        var day = new DaySchedule(date);
        var hall1 = new Hall("Sala 1");
        hall1.Add(new Screening("Diuna", new TimeOnly(18, 0)));
        hall1.Add(new Screening("Diuna", new TimeOnly(21, 0)));
        day.Add(hall1);
        var hall2 = new Hall("Sala 2");
        if (weekend)
        {
            hall2.Add(new Screening("Kraina Lodu", new TimeOnly(10, 0)));
        }
        hall2.Add(new Screening("Amator", new TimeOnly(17, 30)));
        day.Add(hall2);
        var hall3 = new Hall("Sala 3 VIP");
        if (weekend)
        {
            hall3.Add(new Screening("Amator", new TimeOnly(20, 0)));
        }
        day.Add(hall3);
        return day;
    }
}
