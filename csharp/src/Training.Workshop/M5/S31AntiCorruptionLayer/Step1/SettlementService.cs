using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step1;

/// <summary>
/// Krok 1: reguły liczą się na FilmLicense, a rekord dystrybutora jest tłumaczony w jednej
/// prywatnej metodzie, zaraz po pobraniu. Serwis wciąż jednak zna klienta i format dystrybutora.
/// </summary>
public sealed class SettlementService
{
    private readonly DistributorFeed _feed;

    public SettlementService(DistributorFeed feed)
    {
        ArgumentNullException.ThrowIfNull(feed);
        _feed = feed;
    }

    public bool CanScreen(string filmCode, DateOnly day)
    {
        var record = _feed.Fetch(filmCode);
        return record != null && ToLicense(record).AllowsScreeningOn(day);
    }

    public string Settle(string filmCode, DateOnly day, Money revenue)
    {
        var record = _feed.Fetch(filmCode);
        if (record == null)
        {
            return "BRAK LICENCJI " + filmCode;
        }
        var license = ToLicense(record);
        if (!license.Active)
        {
            return "LICENCJA ZAWIESZONA " + filmCode;
        }
        if (!license.Covers(day))
        {
            return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
        }
        return "ROZLICZENIE " + license.Title + " " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + ": przychod " + revenue + ", udzial "
            + license.SharePercent + "%, minimum " + license.MinimumGuarantee
            + ", dla dystrybutora " + license.DueFor(revenue);
    }

    private static FilmLicense ToLicense(DistributorRecord record)
    {
        var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
        return new FilmLicense(
            record.FilmCode,
            title,
            record.StatusCode == "A",
            DateOnly.ParseExact(record.ValidFrom, "yyyyMMdd", CultureInfo.InvariantCulture),
            DateOnly.ParseExact(record.ValidTo, "yyyyMMdd", CultureInfo.InvariantCulture),
            int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture),
            new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100));
    }
}
