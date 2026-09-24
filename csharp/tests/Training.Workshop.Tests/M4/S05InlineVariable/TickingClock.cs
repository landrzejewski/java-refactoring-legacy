namespace Training.Workshop.Tests.M4.S05InlineVariable;

/// <summary>Ręczny fake zegara: każdy odczyt przesuwa czas o sekundę - jak prawdziwy zegar, tylko przewidywalnie.</summary>
internal sealed class TickingClock(DateTimeOffset start) : TimeProvider
{
    private DateTimeOffset _now = start;

    public override DateTimeOffset GetUtcNow()
    {
        var current = _now;
        _now = _now.AddSeconds(1);
        return current;
    }

    public override TimeZoneInfo LocalTimeZone => TimeZoneInfo.Utc;
}
