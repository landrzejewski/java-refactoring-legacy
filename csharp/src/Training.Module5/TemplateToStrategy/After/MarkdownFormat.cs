namespace Training.Module5.TemplateToStrategy.After;

public sealed class MarkdownFormat : INotesFormat
{
    public string Header(string title, string version) => "# " + title + " " + version + "\n";

    public string Entry(ChangeEntry entry) =>
        "- [" + entry.Kind.ToString().ToUpperInvariant() + "] " + entry.Description + "\n";

    public string Footer() => "";
}
