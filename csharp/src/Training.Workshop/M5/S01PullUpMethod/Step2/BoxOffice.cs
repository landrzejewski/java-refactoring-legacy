using Training.Workshop.Shared;

namespace Training.Workshop.M5.S01PullUpMethod.Step2;

/// <summary>Krok 2: bez zmian - klient nadal zna konkretne klasy (Label() jeszcze nie ma w bazie).</summary>
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
