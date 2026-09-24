namespace Training.Workshop.M7.S11MiddleMan;

/// <summary>Stabilny kontrakt sceny: właściwy dostawca danych o seansach. Nieznany seans = wyjątek.</summary>
public sealed class ScreeningCatalog
{
    private readonly OrderedDictionary<string, Screening> _screenings = [];

    public ScreeningCatalog(IEnumerable<Screening> screenings)
    {
        foreach (var screening in screenings)
        {
            _screenings[screening.Id] = screening;
        }
    }

    public string Title(string id) => Find(id).Title;

    public string Format(string id) => Find(id).Format;

    public int FreeSeats(string id) => Find(id).FreeSeats;

    public IReadOnlyList<Screening> All() => _screenings.Values.ToList();

    private Screening Find(string id)
    {
        if (!_screenings.TryGetValue(id, out var screening))
        {
            throw new KeyNotFoundException("brak seansu " + id);
        }
        return screening;
    }
}
