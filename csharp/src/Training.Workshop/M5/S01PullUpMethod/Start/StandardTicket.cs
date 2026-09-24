using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Start;

/// <summary>Start: bilet normalny - etykieta sklejana operatorem +.</summary>
public sealed class StandardTicket : Ticket
{
    public StandardTicket(string title, Money basePrice) : base(title, basePrice)
    {
    }

    public Money Price()
    {
        return BasePrice;
    }

    public string Label()
    {
        return Title + ": " + Price();
    }
}
