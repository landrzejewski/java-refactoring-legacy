namespace Training.Module5.TemplateToStrategy.After;

public sealed class HtmlFormat : INotesFormat
{
    public string Header(string title, string version) =>
        "<h1>" + title + " " + Escape(version) + "</h1>\n<ul>\n";

    public string Entry(ChangeEntry entry) =>
        "<li>[" + entry.Kind.ToString().ToUpperInvariant() + "] " + Escape(entry.Description) + "</li>\n";

    public string Footer() => "</ul>\n";

    private static string Escape(string text) =>
        text.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");
}
