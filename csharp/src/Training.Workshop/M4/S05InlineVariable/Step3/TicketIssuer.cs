using System.Globalization;

namespace Training.Workshop.M4.S05InlineVariable.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): najpierw Rename <c>Money(int)</c> -&gt; <c>MoneyFromGrosze</c>, dopiero potem
/// Inline Variable <c>price</c>. Bez Rename wklejone <c>Money(BasePrice(format))</c> wybrałoby
/// przeciążenie int (grosze) i cena 40 zł stałaby się "0.40". number i issuedAt zostają - celowo.
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
        return new Ticket(code,
            "Bilet " + code + ", cena " + Money(BasePrice(format))
                + ", oplata " + MoneyFromGrosze(OnlineFeeGrosze),
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

    /// <summary>Kwota w groszach - osobna nazwa, osobna jednostka.</summary>
    private static string MoneyFromGrosze(int grosze)
    {
        return string.Create(CultureInfo.InvariantCulture, $"{grosze / 100}.{grosze % 100:D2}");
    }
}
