namespace Training.Workshop.M4.S08MoveField.Start;

/// <summary>
/// Start: próg VIP to cecha SALI, a jest polem SEANSU. Każdy seans w tej samej sali niesie
/// własną kopię i nic nie pilnuje, żeby kopie były zgodne. SeatPricer czyta pole bezpośrednio.
/// </summary>
public sealed class Screening
{
    internal readonly int VipFromRow;

    public Screening(Hall hall, int format, int vipFromRow)
    {
        Hall = hall;
        Format = format;
        VipFromRow = vipFromRow;
    }

    public Hall Hall { get; }

    public int Format { get; }

    public bool IsVip(int row)
    {
        return row >= VipFromRow;
    }
}
