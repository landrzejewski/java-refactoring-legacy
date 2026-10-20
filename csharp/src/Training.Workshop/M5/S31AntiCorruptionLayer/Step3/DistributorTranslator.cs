using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>
/// Krok 3: translator jako część warstwy antykorupcyjnej (internal). Zna konwencje
/// dystrybutora: kody statusu, daty "yyyyMMdd", grosze jako tekst, "45%", tytuły wielkimi
/// literami. Zepsuty rekord zgłasza się tutaj, w języku granicy.
/// </summary>
internal sealed class DistributorTranslator
{
    private const string Active = "A";

    internal FilmLicense ToLicense(DistributorRecord record)
    {
        return new FilmLicense(
            record.FilmCode,
            Title(record.TitleUpper),
            record.StatusCode == Active,
            Date(record.ValidFrom),
            Date(record.ValidTo),
            int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture),
            new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100));
    }

    private static string Title(string upper)
    {
        return upper[0] + upper[1..].ToLowerInvariant();
    }

    private static DateOnly Date(string legacy)
    {
        try
        {
            return DateOnly.ParseExact(legacy, "yyyyMMdd", CultureInfo.InvariantCulture);
        }
        catch (FormatException exception)
        {
            throw new ArgumentException("invalid legacy date: " + legacy, exception);
        }
    }
}
