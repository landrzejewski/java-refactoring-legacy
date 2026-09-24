using System.Globalization;

namespace Training.Workshop.M4.S05InlineVariable.Step2;

/// <summary>
/// Krok 2: Inline Variable <c>holdUntil</c> - bezpieczny, bo inicjalizator czyta ZMIENNĄ issuedAt,
/// a nie zegar. Zmiennych number i issuedAt nie wolno wkleić: każde użycie wywołałoby
/// NextNumber() albo _clock.GetUtcNow() od nowa.
/// </summary>
public sealed class TicketIssuer
{
    private static readonly TimeSpan Hold = TimeSpan.FromMinutes(15);
    private const int OnlineFeeGrosze = 200;

    private readonly TimeProvider _clock;
    private int _lastNumber;

    public TicketIssuer(TimeProvider clock)
    {
        _clock = clock;
    }

    public Ticket Issue(string screeningCode, int format)
    {
        int number = NextNumber();
        DateTimeOffset issuedAt = _clock.GetUtcNow();
        string code = screeningCode + "-" + number;
        double price = BasePrice(format);
        return new Ticket(code,
            "Bilet " + code + ", cena " + Money(price)
                + ", oplata " + Money(OnlineFeeGrosze),
            issuedAt, issuedAt + Hold);
    }

    private int NextNumber()
    {
        _lastNumber++;
        return _lastNumber;
    }

    private static int BasePrice(int format)
    {
        return format switch
        {
            3 => 40,
            2 => 32,
            _ => 25,
        };
    }

    /// <summary>Kwota w złotych.</summary>
    private static string Money(double zloty)
    {
        return zloty.ToString("F2", CultureInfo.InvariantCulture);
    }

    /// <summary>Kwota w groszach - ta sama nazwa, inna jednostka.</summary>
    private static string Money(int grosze)
    {
        return string.Create(CultureInfo.InvariantCulture, $"{grosze / 100}.{grosze % 100:D2}");
    }
}
