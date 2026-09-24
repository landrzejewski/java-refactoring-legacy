namespace Training.Workshop.M4.S08MoveField.Step1;

/// <summary>
/// Krok 1: Self-Encapsulate Field - pole prywatne, WSZYSTKIE odczyty (także wewnątrz klasy)
/// idą przez właściwość <c>VipFromRow</c>. Teraz jest jedno miejsce, w którym zmienimy źródło wartości.
/// </summary>
public sealed class Screening
{
    private readonly int _vipFromRow;

    public Screening(Hall hall, int format, int vipFromRow)
    {
        Hall = hall;
        Format = format;
        _vipFromRow = vipFromRow;
    }

    public Hall Hall { get; }

    public int Format { get; }

    public int VipFromRow => _vipFromRow;

    public bool IsVip(int row)
    {
        return row >= VipFromRow;
    }
}
