namespace Training.Workshop.M5.S02PullUpField.Start;

/// <summary>Start: miejsce ustawiane setterem po utworzeniu - pole mutowalne.</summary>
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
