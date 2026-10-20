namespace Training.Workshop.M5.S31AntiCorruptionLayer;

/// <summary>
/// Klient systemu dystrybutora (nie zmieniamy go): zwraca rekord albo null, gdy filmu nie zna.
/// Kod bez adnotacji nullable (jak cudza biblioteka sprzed NRT): kompilator nie ostrzega,
/// że <see cref="Fetch"/> może zwrócić null.
/// </summary>
#nullable disable
public class DistributorFeed
{
    private readonly Dictionary<string, DistributorRecord> _records = [];

    public DistributorFeed(IEnumerable<DistributorRecord> records)
    {
        foreach (var record in records)
        {
            _records[record.FilmCode] = record;
        }
    }

    public virtual DistributorRecord Fetch(string filmCode)
    {
        return _records.GetValueOrDefault(filmCode);
    }
}
