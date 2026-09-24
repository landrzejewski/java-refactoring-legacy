using System.Globalization;

namespace Training.Workshop.M8.S03ParallelRun;

/// <summary>Stabilny kontrakt sceny: pytanie o cenę jednego biletu.</summary>
/// <param name="Format">2D, 3D, IMAX (inne formaty legacy wycenia na 0.00)</param>
/// <param name="Type">NORMAL, STUDENT, SENIOR, CHILD</param>
/// <param name="Row">rząd miejsca (10 i dalej to VIP)</param>
public sealed record TicketQuery(string Format, string Type, TimeOnly Start, int Row)
{
    public override string ToString()
    {
        return Format + " " + Type + " " + Start.ToString("HH:mm", CultureInfo.InvariantCulture) + " rzad " + Row;
    }
}
