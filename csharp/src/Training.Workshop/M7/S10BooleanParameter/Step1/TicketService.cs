using System.Globalization;

namespace Training.Workshop.M7.S10BooleanParameter.Step1;

/// <summary>
/// Krok 1: flaga online zastąpiona jawnymi metodami BookOnline / BookAtBoxOffice.
/// Stara metoda zostaje jako [Obsolete] i deleguje - klienci migrują po jednym.
/// Stara sygnatura zostaje jako osobne przeciążenie: skompilowany wcześniej klient z innego
/// assembly odwołuje się do niej w IL, więc jej usunięcie łamie zgodność binarną.
/// </summary>
public sealed class TicketService
{
    private const decimal GlassesPrice = 3.00m;
    private const decimal Fee = 2.00m;

    private enum Channel { Online, BoxOffice }

    /// <summary>Przestarzałe: użyj <see cref="BookOnline"/> albo <see cref="BookAtBoxOffice"/>.</summary>
    [Obsolete("użyj BookOnline albo BookAtBoxOffice")]
    public string Book(string title, string format, int seats, bool online, bool ownGlasses)
    {
        return online
            ? BookOnline(title, format, seats, ownGlasses)
            : BookAtBoxOffice(title, format, seats, ownGlasses);
    }

    public string BookOnline(string title, string format, int seats, bool ownGlasses)
    {
        return Book(title, format, seats, Channel.Online, ownGlasses);
    }

    public string BookAtBoxOffice(string title, string format, int seats, bool ownGlasses)
    {
        return Book(title, format, seats, Channel.BoxOffice, ownGlasses);
    }

    private string Book(string title, string format, int seats, Channel channel, bool ownGlasses)
    {
        var basePrice = format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        decimal count = seats;
        var total = basePrice * count;
        if (format == "3D" && !ownGlasses)
        {
            total += GlassesPrice * count;
        }
        if (channel == Channel.Online)
        {
            total += Fee * count;
        }
        return title + " " + format + " x" + seats
            + (channel == Channel.Online ? " online" : " kasa") + ": "
            + total.ToString(CultureInfo.InvariantCulture);
    }
}
