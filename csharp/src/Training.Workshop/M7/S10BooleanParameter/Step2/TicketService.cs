using System.Globalization;

namespace Training.Workshop.M7.S10BooleanParameter.Step2;

/// <summary>
/// Krok 2: druga flaga (ownGlasses) w nowych metodach zamieniona na enum Glasses.
/// Nie dokładamy metody na każdą kombinację (4 metody) - kanał to metoda, okulary to wartość.
/// Stara metoda [Obsolete] tłumaczy bool na Glasses.
/// Uwaga C#: kusi skrót "Glasses glasses = Glasses.Rented" (parametr opcjonalny). Wartość domyślna
/// jest wkompilowana w miejsce wywołania klienta, a dodanie parametru opcjonalnego do istniejącej
/// metody zmienia jej sygnaturę w IL - to łamie zgodność binarną (patrz S10BinaryCompatibilityTest).
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
        var glasses = ownGlasses ? Glasses.Own : Glasses.Rented;
        return online
            ? BookOnline(title, format, seats, glasses)
            : BookAtBoxOffice(title, format, seats, glasses);
    }

    public string BookOnline(string title, string format, int seats, Glasses glasses)
    {
        return Book(title, format, seats, Channel.Online, glasses);
    }

    public string BookAtBoxOffice(string title, string format, int seats, Glasses glasses)
    {
        return Book(title, format, seats, Channel.BoxOffice, glasses);
    }

    private string Book(string title, string format, int seats, Channel channel, Glasses glasses)
    {
        var basePrice = format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        decimal count = seats;
        var total = basePrice * count;
        if (format == "3D" && glasses == Glasses.Rented)
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
