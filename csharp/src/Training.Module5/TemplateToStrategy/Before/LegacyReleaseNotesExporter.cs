namespace Training.Module5.TemplateToStrategy.Before;

/// <summary>
/// Template Method z dwiema niezależnymi osiami (format i zakres treści) zaszytymi
/// w podklasach. Każda kombinacja to osobna klasa: 2 formaty x 2 zakresy = 4 podklasy,
/// a kolejny format dokłada następne dwie.
/// </summary>
public abstract class LegacyReleaseNotesExporter
{
    public string Export(string version, IEnumerable<ChangeEntry> entries)
    {
        var output = new System.Text.StringBuilder(Header(version));
        foreach (var entry in entries)
        {
            if (Include(entry))
            {
                output.Append(FormatEntry(entry));
            }
        }
        return output.Append(Footer()).ToString();
    }

    protected abstract string Header(string version);

    protected abstract bool Include(ChangeEntry entry);

    protected abstract string FormatEntry(ChangeEntry entry);

    protected abstract string Footer();
}
