using System.Globalization;

namespace Training.Workshop.M7.S10BooleanParameter.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): wszyscy klienci zmigrowani, kompilator nie zgłasza już ostrzeżeń
/// CS0618 o przestarzałym API, więc Book(..., bool, bool) usunięte (Safe Delete).
/// Uwaga: w bibliotece publicznej usunięcie łamie zgodność binarną (stary klient dostaje
/// MissingMethodException) - tu to decyzja o końcu okresu przejściowego.
/// </summary>
public sealed class TicketService
{
    private const decimal GlassesPrice = 3.00m;
    private const decimal Fee = 2.00m;

    private enum Channel { Online, BoxOffice }

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
