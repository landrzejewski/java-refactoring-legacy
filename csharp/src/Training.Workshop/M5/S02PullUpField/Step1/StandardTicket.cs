namespace Training.Workshop.M5.S02PullUpField.Step1;

/// <summary>Krok 1: bez zmian - miejsce nadal ustawiane setterem.</summary>
public sealed class StandardTicket : Ticket
{
    private string? _seat;

    public string? Seat
    {
        get => _seat;
        set => _seat = value;
    }

    public override string Describe()
    {
        return "NORMAL " + _seat;
    }
}
