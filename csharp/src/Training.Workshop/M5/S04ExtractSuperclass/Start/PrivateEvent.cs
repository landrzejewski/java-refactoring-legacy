namespace Training.Workshop.M5.S04ExtractSuperclass.Start;

/// <summary>Start: wynajem sali - te same cztery pojęcia co w Screening, skopiowane.</summary>
public sealed class PrivateEvent
{
    private readonly string _client;
    private readonly string _hall;
    private readonly DateTime _start;
    private readonly int _minutes;

    public PrivateEvent(string client, string hall, DateTime start, int minutes)
    {
        _client = client;
        _hall = hall;
        _start = start;
        _minutes = minutes;
    }

    /// <summary>Fabryka: standardowy wynajem trwa 2 godziny.</summary>
    public static PrivateEvent Rental(string client, string hall, DateTime start)
    {
        return new PrivateEvent(client, hall, start, 120);
    }

    public string Client => _client;

    public string Hall => _hall;

    public DateTime Start => _start;

    public DateTime End => _start.AddMinutes(_minutes);
}
