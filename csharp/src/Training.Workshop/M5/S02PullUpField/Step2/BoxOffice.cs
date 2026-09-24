namespace Training.Workshop.M5.S02PullUpField.Step2;

/// <summary>Krok 2: klient tworzy każdy bilet jednym wywołaniem konstruktora.</summary>
public sealed class BoxOffice
{
    public string Describe(string kind, string seat, string? studentId)
    {
        return kind switch
        {
            "STUDENT" => new StudentTicket(seat, studentId).Describe(),
            "VIP" => new VipTicket(seat).Describe(),
            _ => new StandardTicket(seat).Describe(),
        };
    }
}
