namespace Training.Workshop.M4.S08MoveField.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): aktualizacja odczytów. Reguła "rząd &gt;= próg" trafiła do Hall.IsVip,
/// SeatPricer pyta o VIP-owość zamiast czytać surowy próg, a przejściowa właściwość
/// <c>Screening.VipFromRow</c> została usunięta (Safe Delete). Jedno źródło prawdy.
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

    public bool IsVip(int row)
    {
        return Hall.IsVip(row);
    }
}
