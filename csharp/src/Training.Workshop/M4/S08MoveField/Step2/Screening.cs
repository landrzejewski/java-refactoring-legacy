namespace Training.Workshop.M4.S08MoveField.Step2;

/// <summary>
/// Krok 2: Move Field - <c>VipFromRow</c> przeniesione do Hall. Screening nie ma już pola,
/// a jego właściwość deleguje do sali. Bez okresu przejściowego z dwiema kopiami (dual write).
/// Konstruktor stracił parametr - to zmiana dla wszystkich miejsc tworzących seanse.
/// </summary>
public sealed class Screening
{
    public Screening(Hall hall, int format)
    {
        Hall = hall;
        Format = format;
    }

    public Hall Hall { get; }

    public int Format { get; }

    public int VipFromRow => Hall.VipFromRow;

    public bool IsVip(int row)
    {
        return row >= VipFromRow;
    }
}
