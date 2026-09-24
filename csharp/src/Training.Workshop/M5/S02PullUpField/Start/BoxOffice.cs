namespace Training.Workshop.M5.S02PullUpField.Start;

/// <summary>Start: klient tworzy bilet normalny w dwóch krokach (konstruktor + setter właściwości).</summary>
public sealed class BoxOffice
{
    public string Describe(string kind, string seat, string? studentId)
    {
        return kind switch
        {
            "STUDENT" => new StudentTicket(seat, studentId).Describe(),
            "VIP" => new VipTicket(seat).Describe(),
            _ => new StandardTicket { Seat = seat }.Describe(),
        };
    }
}
