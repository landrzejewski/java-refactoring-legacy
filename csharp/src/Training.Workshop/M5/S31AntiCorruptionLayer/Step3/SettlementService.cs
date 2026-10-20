using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>
/// Krok 3: serwis domeny zależy tylko od portu IFilmLicenses i mówi wyłącznie językiem kina.
/// Żaden typ dystrybutora nie przekracza warstwy antykorupcyjnej (DistributorAcl).
/// </summary>
public sealed class SettlementService
{
    private readonly IFilmLicenses _licenses;

    public SettlementService(IFilmLicenses licenses)
    {
        ArgumentNullException.ThrowIfNull(licenses);
        _licenses = licenses;
    }

    public bool CanScreen(string filmCode, DateOnly day)
    {
        return _licenses.Find(filmCode)?.AllowsScreeningOn(day) ?? false;
    }

    public string Settle(string filmCode, DateOnly day, Money revenue)
    {
        var license = _licenses.Find(filmCode);
        if (license == null)
        {
            return "BRAK LICENCJI " + filmCode;
        }
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
