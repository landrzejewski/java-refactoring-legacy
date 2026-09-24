namespace Training.Workshop.M5.S02PullUpField.Step1;

/// <summary>Krok 1: bez zmian - klient nie używał SeatCode.</summary>
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
