using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step2;

/// <summary>
/// Krok 2: serwis nie zna już konwencji dystrybutora - tłumaczy je DistributorTranslator.
/// Nadal jednak pobiera DistributorRecord i obsługuje jego null; schowamy to za portem w kroku 3.
/// </summary>
public sealed class SettlementService
{
    private readonly DistributorFeed _feed;
    private readonly DistributorTranslator _translator = new();

    public SettlementService(DistributorFeed feed)
    {
        ArgumentNullException.ThrowIfNull(feed);
        _feed = feed;
    }

    public bool CanScreen(string filmCode, DateOnly day)
    {
        var record = _feed.Fetch(filmCode);
        return record != null && _translator.ToLicense(record).AllowsScreeningOn(day);
    }

    public string Settle(string filmCode, DateOnly day, Money revenue)
    {
        var record = _feed.Fetch(filmCode);
        if (record == null)
        {
            return "BRAK LICENCJI " + filmCode;
        }
        var license = _translator.ToLicense(record);
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
}
