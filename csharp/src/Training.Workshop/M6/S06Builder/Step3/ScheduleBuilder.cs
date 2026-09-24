namespace Training.Workshop.M6.S06Builder.Step3;

/// <summary>
/// Krok 3: zagnieżdżony builder - sala konfigurowana lambdą, więc znika ukryty stan
/// "bieżąca sala". Lambda jest wywoływana synchronicznie, dokładnie raz.
/// </summary>
public sealed class ScheduleBuilder
{
    private readonly DateOnly _date;
    private readonly List<Hall> _halls = [];
    private bool _built;

    private ScheduleBuilder(DateOnly date)
    {
        _date = date;
    }

    public static ScheduleBuilder Day(DateOnly date)
    {
        return new ScheduleBuilder(date);
    }

    public ScheduleBuilder Hall(string name, Action<HallBuilder> content)
    {
        RequireNotBuilt();
        if (_halls.Any(hall => hall.Name == name))
        {
            throw new InvalidOperationException("duplicate hall: " + name);
        }
        var hall = new HallBuilder();
        content(hall);
        _halls.Add(new Hall(name, hall.Screenings));
        return this;
    }

    public DaySchedule Build()
    {
        RequireNotBuilt();
        _built = true;
        return new DaySchedule(_date, _halls);
    }

    private void RequireNotBuilt()
    {
        if (_built)
        {
            throw new InvalidOperationException("builder already used");
        }
    }

    public sealed class HallBuilder
    {
        private readonly List<Screening> _screenings = [];

        internal HallBuilder()
        {
        }

        internal IReadOnlyList<Screening> Screenings => _screenings;

        public HallBuilder Screening(string title, int hour, int minute)
        {
            _screenings.Add(new Screening(title, new TimeOnly(hour, minute)));
            return this;
        }
    }
}
