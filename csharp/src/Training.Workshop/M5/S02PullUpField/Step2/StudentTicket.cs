namespace Training.Workshop.M5.S02PullUpField.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public sealed class StudentTicket : Ticket
{
    private readonly string _seat;
    private readonly string? _studentId;

    public StudentTicket(string seat, string? studentId)
    {
        _seat = seat;
        _studentId = studentId;
    }

    public string Seat => _seat;

    public override string Describe()
    {
        return "STUDENT " + _seat + " (legitymacja " + _studentId + ")";
    }
}
