namespace Training.Workshop.M5.S02PullUpField.Step3;

/// <summary>Krok 3: stan miejsca przeniesiony do bazy, podklasa tylko go opisuje.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string seat) : base(seat)
    {
    }

    public override string Describe()
    {
        return "NORMAL " + Seat;
    }
}
