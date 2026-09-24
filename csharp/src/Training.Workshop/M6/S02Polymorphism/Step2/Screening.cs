using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step2;

/// <summary>
/// Krok 2: pozostałe gałęzie w podklasach, Screening jest abstrakcyjny, pole _kind i enum Kind
/// usunięte. Jedyny switch został w miejscu tworzenia (mapowanie wiersza z bazy).
/// </summary>
public abstract class Screening
{
    private readonly string _title;

    private protected Screening(string title)
    {
        _title = title;
    }

    public static Screening FromRow(ScreeningRow row)
    {
        return row.Kind switch
        {
            "REGULAR" => new RegularScreening(row.Title, row.Value),
            "PREMIERE" => new PremiereScreening(row.Title, row.Value),
            "MARATHON" => new MarathonScreening(row.Title, row.Value),
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
    }

    protected string Title => _title;

    public abstract string Label();

    public abstract int DurationMinutes();

    public abstract Money Price();
}
