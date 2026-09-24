using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>Krok 3: czysty rdzeń - tylko to, co ma każdy bilet. Bez flag.</summary>
public sealed record Ticket(string Title, string Format, Money Base) : IPricedTicket
{
    public Money Price()
    {
        return Base;
    }

    public string Description()
    {
        return Title + " " + Format;
    }
}
