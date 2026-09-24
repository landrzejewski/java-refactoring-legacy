using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S12ExpandContract.Start;

/// <summary>
/// Start: stary format wiersza - B1;anna@kino.pl;A5,A10;84.00
/// (bez wersji, średnik w danych psuje wiersz).
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
