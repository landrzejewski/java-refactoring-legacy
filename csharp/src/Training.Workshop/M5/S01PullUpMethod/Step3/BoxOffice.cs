using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step3;

/// <summary>Krok 3: klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym.</summary>
public sealed class BoxOffice
{
    public string Label(string kind, string title, Money basePrice)
    {
        Ticket ticket = kind switch
        {
            "STUDENT" => new StudentTicket(title, basePrice),
            "VIP" => new VipTicket(title, basePrice),
            _ => new StandardTicket(title, basePrice),
        };
        return ticket.Label();
    }
}
