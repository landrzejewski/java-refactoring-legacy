namespace Training.Workshop.M6.S06Builder.Step2;

/// <summary>
/// Krok 2: builder zbiera dane, a drzewo (rekordy) powstaje w Build(). Builder jest jednorazowy -
/// drugie Build() rzuca wyjątek, bo wynik nie może zależeć od późniejszych wywołań.
/// </summary>
public sealed class ScheduleBuilder
{
    private readonly DateOnly _date;
    private readonly OrderedDictionary<string, List<Screening>> _halls = [];
    private List<Screening>? _current;
    private bool _built;

    public ScheduleBuilder(DateOnly date)
    {
        _date = date;
    }

    public ScheduleBuilder Hall(string name)
    {
        RequireNotBuilt();
        if (_halls.ContainsKey(name))
        {
            throw new InvalidOperationException("duplicate hall: " + name);
        }
        _current = [];
        _halls.Add(name, _current);
        return this;
    }

    public ScheduleBuilder Screening(string title, int hour, int minute)
    {
        RequireNotBuilt();
        if (_current == null)
        {
            throw new InvalidOperationException("screening without hall");
        }
        _current.Add(new Screening(title, new TimeOnly(hour, minute)));
        return this;
    }

    public DaySchedule Build()
    {
        RequireNotBuilt();
        _built = true;
        var result = new List<Hall>();
        foreach (var (name, screenings) in _halls)
        {
            result.Add(new Hall(name, screenings));
        }
        return new DaySchedule(_date, result);
    }

    private void RequireNotBuilt()
    {
        if (_built)
        {
            throw new InvalidOperationException("builder already used");
        }
    }
}
