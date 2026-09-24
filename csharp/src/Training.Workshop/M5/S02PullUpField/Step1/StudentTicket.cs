namespace Training.Workshop.M5.S02PullUpField.Step1;

/// <summary>Krok 1: Rename (pole _seatCode i akcesor SeatCode) na _seat/Seat - ta sama nazwa dla tego samego znaczenia.</summary>
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
