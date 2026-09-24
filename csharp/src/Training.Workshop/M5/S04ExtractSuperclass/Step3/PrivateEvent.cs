namespace Training.Workshop.M5.S04ExtractSuperclass.Step3;

/// <summary>Krok 3: PrivateEvent podaje tylko swoją nazwę do opisu konfliktu.</summary>
public sealed class PrivateEvent : HallBooking
{
    private readonly string _client;

    public PrivateEvent(string client, string hall, DateTime start, int minutes) : base(hall, start, minutes)
    {
        _client = client;
    }

    /// <summary>Fabryka: standardowy wynajem trwa 2 godziny.</summary>
    public static PrivateEvent Rental(string client, string hall, DateTime start)
    {
        return new PrivateEvent(client, hall, start, 120);
    }

    public string Client => _client;

    public override string Name => "Wynajem: " + _client;
}
