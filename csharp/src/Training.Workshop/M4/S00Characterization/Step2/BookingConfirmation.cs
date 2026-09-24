using System.Globalization;

namespace Training.Workshop.M4.S00Characterization.Step2;

/// <summary>
/// Krok 2: pierwsza refaktoryzacja pod ochroną testu - Extract Method <c>TicketPrice</c>.
/// Świadomie NIE poprawiamy znalezisk z charakterystyki: <c>double</c>, <c>ToString("F2")</c>
/// bez jawnej kultury i rabatu grupowego dopiero od 11 biletów. To osobne decyzje, nie refaktoryzacja.
/// </summary>
public sealed class BookingConfirmation
{
    private readonly TimeProvider _clock;

    public BookingConfirmation()
        : this(TimeProvider.System)
    {
    }

    public BookingConfirmation(TimeProvider clock)
    {
        _clock = clock;
    }

    public string Confirm(Booking b)
    {
        double sum = 0;
        foreach (var t in b.TicketTypes)
        {
            sum = sum + TicketPrice(b, t);
        }
        if (b.TicketTypes.Count > 10)
        {
            sum = sum * 0.9;
        }
        double fee = b.Online ? 2.00 * b.TicketTypes.Count : 0;
        string f = b.Format == 3 ? "IMAX" : b.Format == 2 ? "3D" : "2D";
        return "POTWIERDZENIE REZERWACJI\n"
            + "Klient: " + b.Customer.Trim().ToUpper() + "\n"
            + "Film: " + b.Title + ", " + f + ", " + b.Start.ToString("HH:mm", CultureInfo.InvariantCulture) + "\n"
            + "Bilety: " + b.TicketTypes.Count + " [" + string.Join(", ", b.TicketTypes) + "]\n"
            + "Bilety razem: " + sum.ToString("F2") + "\n"
            + "Oplata rezerwacyjna: " + fee.ToString("F2") + "\n"
            + "Do zaplaty: " + (sum + fee).ToString("F2") + "\n"
            + "Wygenerowano: " + _clock.GetLocalNow().DateTime.ToString("yyyy-MM-dd'T'HH:mm:ss", CultureInfo.InvariantCulture) + "\n";
    }

    private double TicketPrice(Booking b, string t)
    {
        double p;
        if (b.Format == 3)
        {
            p = 40.00;
        }
        else if (b.Format == 2)
        {
            p = 32.00;
        }
        else
        {
            p = 25.00;
        }
        if (t == "S")
        {
            p = p * 0.75;
        }
        else if (t == "E")
        {
            p = p * 0.70;
        }
        else if (t == "C")
        {
            p = p * 0.60;
        }
        if (b.Start.Hour < 12)
        {
            p = p - 5.00;
        }
        return p;
    }
}
