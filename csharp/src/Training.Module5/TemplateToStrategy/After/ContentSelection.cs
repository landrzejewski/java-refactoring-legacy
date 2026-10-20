namespace Training.Module5.TemplateToStrategy.After;

/// <summary>
/// Strategia zakresu treści - dawny krok <c>Include</c> szablonu razem z tytułem.
/// </summary>
public sealed record ContentSelection
{
    public static readonly ContentSelection Changelog =
        new("Changelog", _ => true);

    public static readonly ContentSelection Highlights =
        new("Highlights", entry => entry.Kind != Kind.Fix);

    public ContentSelection(string? title, Func<ChangeEntry, bool> filter)
    {
        if (string.IsNullOrWhiteSpace(title))
        {
            throw new ArgumentException("title must not be blank");
        }
        ArgumentNullException.ThrowIfNull(filter);
        Title = title;
        Filter = filter;
    }

    public string Title { get; }

    public Func<ChangeEntry, bool> Filter { get; }

    public bool Includes(ChangeEntry entry) => Filter(entry);
}
