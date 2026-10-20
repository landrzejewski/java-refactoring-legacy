namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>
/// Krok 3: Anti-Corruption Layer - adapter portu IFilmLicenses nad klientem dystrybutora
/// plus translator modelu. Tylko ta warstwa widzi DistributorFeed i DistributorRecord.
/// </summary>
/// <remarks>
/// Facade a ACL: fasada upraszcza dostęp do podsystemu, ale mówi jego językiem
/// (te same pojęcia, mniej wywołań). ACL tłumaczy cudzy model na własny i chroni domenę
/// przed cudzymi pojęciami - często jest zbudowana z fasady, adaptera i translatora naraz.
/// </remarks>
public sealed class DistributorAcl : IFilmLicenses
{
    private readonly DistributorFeed _feed;
    private readonly DistributorTranslator _translator = new();

    public DistributorAcl(DistributorFeed feed)
    {
        ArgumentNullException.ThrowIfNull(feed);
        _feed = feed;
    }

    public FilmLicense? Find(string filmCode)
    {
        var record = _feed.Fetch(filmCode);
        return record == null ? null : _translator.ToLicense(record);
    }
}
