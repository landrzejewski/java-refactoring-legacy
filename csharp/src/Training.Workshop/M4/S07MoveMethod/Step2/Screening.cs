using System.Globalization;

namespace Training.Workshop.M4.S07MoveMethod.Step2;

/// <summary>
/// Krok 2: Screening przejął też listę wolnych miejsc
/// (Move Method z BookingPrinter.RemainingSeats).
/// </summary>
/// <param name="Title">tytuł filmu</param>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Start">początek seansu</param>
/// <param name="Hall">numer sali</param>
/// <param name="FreeSeats">numery wolnych miejsc (kolejność ze starego systemu, nie zawsze rosnąca)</param>
public sealed record Screening(string Title, int Format, DateTime Start, int Hall,
    IReadOnlyList<int> FreeSeats)
{
    public string Headline()
    {
        string formatName = Format switch
        {
            3 => "IMAX",
            2 => "3D",
            _ => "2D",
        };
        return Title + " (" + formatName + "), sala " + Hall + ", "
            + Start.ToString("yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture);
    }

    /// <summary>
    /// Wolne miejsca bez wskazanego. Parametr MUSI zostać <c>int?</c>: z <c>int</c>
    /// wywołanie <c>Remove</c> wybrałoby <c>Remove(int index)</c> zamiast <c>Remove(int? seat)</c>.
    /// </summary>
    public SeatList FreeSeatsWithout(int? seat)
    {
        var free = new SeatList(FreeSeats);
        free.Remove(seat);
        return free;
    }
}
