using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S12ExpandContract.Step1;

/// <summary>
/// Krok 1 (bez zmian): stary format wiersza - nadal zapisywany, żeby stara wersja mogła czytać dane.
/// </summary>
public static class CsvBookingFormat
{
    public static string Write(Booking booking)
    {
        return booking.Id + ";" + booking.Email + ";" + string.Join(",", booking.Seats)
            + ";" + booking.Total;
    }

    public static Booking Read(string line)
    {
        string[] parts = line.Split(';');
        return new Booking(parts[0], parts[1], parts[2].Split(','),
            new Money(decimal.Parse(parts[3], CultureInfo.InvariantCulture)));
    }
}
