using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S12ExpandContract.Step3;

/// <summary>Krok 3 (bez zmian): nowy, wersjonowany format payload.</summary>
public static class BookingPayloadFormat
{
    private const string Version = "v2";

    public static string Write(Booking booking)
    {
        return Version + "|id=" + booking.Id + "|email=" + booking.Email
            + "|seats=" + string.Join(" ", booking.Seats) + "|total=" + booking.Total;
    }

    public static Booking Read(string payload)
    {
        string[] parts = payload.Split('|');
        if (!parts[0].Equals(Version))
        {
            throw new ArgumentException("Nieznana wersja formatu: " + parts[0]);
        }
        var fields = new Dictionary<string, string>();
        for (int i = 1; i < parts.Length; i++)
        {
            string[] field = parts[i].Split('=', 2);
            fields[field[0]] = field[1];
        }
        return new Booking(fields["id"], fields["email"],
            fields["seats"].Split(' '),
            new Money(decimal.Parse(fields["total"], CultureInfo.InvariantCulture)));
    }
}
