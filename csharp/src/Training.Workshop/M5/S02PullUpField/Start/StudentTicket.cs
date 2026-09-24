namespace Training.Workshop.M5.S02PullUpField.Start;

/// <summary>Start: to samo znaczenie co "seat", ale inna nazwa. _studentId to inne pojęcie - zostaje tutaj.</summary>
public sealed class StudentTicket : Ticket
{
    private readonly string _seatCode;
    private readonly string? _studentId;

    public StudentTicket(string seatCode, string? studentId)
    {
        _seatCode = seatCode;
        _studentId = studentId;
    }

    public string SeatCode => _seatCode;

    public override string Describe()
    {
        return "STUDENT " + _seatCode + " (legitymacja " + _studentId + ")";
    }
}
