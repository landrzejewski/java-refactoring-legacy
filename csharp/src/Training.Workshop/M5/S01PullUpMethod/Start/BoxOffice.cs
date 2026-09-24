using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Start;

/// <summary>Start: klient musi znać konkretną klasę, bo <c>Label()</c> nie istnieje w typie bazowym.</summary>
public sealed class BoxOffice
{
    public string Label(string kind, string title, Money basePrice)
    {
        return kind switch
        {
            "STUDENT" => new StudentTicket(title, basePrice).Label(),
            "VIP" => new VipTicket(title, basePrice).Label(),
            _ => new StandardTicket(title, basePrice).Label(),
        };
    }
}
