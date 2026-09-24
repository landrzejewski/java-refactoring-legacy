namespace Training.Workshop.M6.S06Builder.Step1;

/// <summary>
/// Krok 1: klasyczny Builder (Encapsulate Composite with Builder) - pamięta bieżącą salę,
/// więc klient nie operuje węzłami ani Add. Drzewo pod spodem bez zmian.
/// </summary>
public sealed class ScheduleBuilder
{
    private readonly DaySchedule _day;
    private Hall? _current;

    public ScheduleBuilder(DateOnly date)
    {
        _day = new DaySchedule(date);
    }

    public ScheduleBuilder Hall(string name)
    {
        _current = new Hall(name);
        _day.Add(_current);
        return this;
    }

    public ScheduleBuilder Screening(string title, int hour, int minute)
    {
        if (_current == null)
        {
            throw new InvalidOperationException("screening without hall");
        }
        _current.Add(new Screening(title, new TimeOnly(hour, minute)));
        return this;
    }

    public DaySchedule Build()
    {
        return _day;
    }
}
