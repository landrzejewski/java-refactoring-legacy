using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step3;

/// <summary>
/// Krok 3: forma C# - abstrakcyjny rekord i zapieczętowane rekordy podtypów. Konstruktor
/// <c>private protected</c> zamyka zestaw rodzajów w tym assembly, ale (inaczej niż <c>sealed interface</c>
/// w Javie) kompilator C# nie sprawdza wyczerpujących switchy po typach - pilnuje tego test.
/// </summary>
public abstract record Screening
{
    private protected Screening()
    {
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

    public abstract string Title { get; }

    public abstract string Label();

    public abstract int DurationMinutes();

    public abstract Money Price();
}
