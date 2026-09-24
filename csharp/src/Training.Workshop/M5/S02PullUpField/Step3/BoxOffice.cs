namespace Training.Workshop.M5.S02PullUpField.Step3;

/// <summary>Krok 3: bez zmian - klient nie zauważył przeniesienia pola.</summary>
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
