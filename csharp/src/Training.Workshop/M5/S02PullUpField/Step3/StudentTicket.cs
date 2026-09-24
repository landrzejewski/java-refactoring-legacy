namespace Training.Workshop.M5.S02PullUpField.Step3;

/// <summary>Krok 3: _seat w bazie; _studentId ma inne znaczenie, więc zostaje w podklasie.</summary>
public sealed class StudentTicket : Ticket
{
    private readonly string? _studentId;

    public StudentTicket(string seat, string? studentId) : base(seat)
    {
        _studentId = studentId;
    }

    public override string Describe()
    {
        return "STUDENT " + Seat + " (legitymacja " + _studentId + ")";
    }
}
