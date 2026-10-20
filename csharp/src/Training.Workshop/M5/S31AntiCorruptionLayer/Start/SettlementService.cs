using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Start;

/// <summary>
/// Start: rozliczenie z dystrybutorem myśli w modelu dystrybutora. Logika domeny (czy wolno
/// grać film, ile się należy) jest przemieszana z dekodowaniem cudzych konwencji: kody statusu,
/// daty "yyyyMMdd", grosze jako tekst, "45%", tytuły wielkimi literami. Każda zmiana formatu
/// u dystrybutora rozlewa się po naszych regułach.
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
        return record != null
            && record.StatusCode == "A"
            && day >= DateOnly.ParseExact(record.ValidFrom, "yyyyMMdd", CultureInfo.InvariantCulture)
            && day <= DateOnly.ParseExact(record.ValidTo, "yyyyMMdd", CultureInfo.InvariantCulture);
    }

    public string Settle(string filmCode, DateOnly day, Money revenue)
    {
        var record = _feed.Fetch(filmCode);
        if (record == null)
        {
            return "BRAK LICENCJI " + filmCode;
        }
        if (record.StatusCode != "A")
        {
            return "LICENCJA ZAWIESZONA " + filmCode;
        }
        if (!CanScreen(filmCode, day))
        {
            return "LICENCJA NIEWAZNA " + filmCode + " w dniu " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
        }
        var percent = int.Parse(record.Share.Replace("%", "").Trim(), CultureInfo.InvariantCulture);
        var minimum = new Money(decimal.Parse(record.MinGuaranteeCents, CultureInfo.InvariantCulture) / 100);
        var due = revenue.Percent(percent).Max(minimum);
        var title = record.TitleUpper[0] + record.TitleUpper[1..].ToLowerInvariant();
        return "ROZLICZENIE " + title + " " + day.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + ": przychod " + revenue + ", udzial " + percent
            + "%, minimum " + minimum + ", dla dystrybutora " + due;
    }
}
