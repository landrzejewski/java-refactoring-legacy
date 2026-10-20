namespace Training.Module5.TemplateToStrategy.After;

/// <summary>
/// Replace Inheritance with Delegation (Template Method -> Strategy): szkielet algorytmu
/// zostaje w jednej finalnej klasie, a zmienne kroki dostarczają dwie niezależne
/// strategie. N formatów i M zakresów to N + M klas zamiast N x M podklas.
/// </summary>
public sealed class ReleaseNotesExporter
{
    private readonly INotesFormat _format;
    private readonly ContentSelection _selection;

    public ReleaseNotesExporter(INotesFormat format, ContentSelection selection)
    {
        ArgumentNullException.ThrowIfNull(format);
        ArgumentNullException.ThrowIfNull(selection);
        _format = format;
        _selection = selection;
    }

    public string Export(string version, IEnumerable<ChangeEntry> entries)
    {
        var output = new System.Text.StringBuilder(_format.Header(_selection.Title, version));
        foreach (var entry in entries)
        {
            if (_selection.Includes(entry))
            {
                output.Append(_format.Entry(entry));
            }
        }
        return output.Append(_format.Footer()).ToString();
    }
}
