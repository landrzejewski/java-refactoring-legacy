namespace Training.Module5.TemplateToStrategy.Before;

public class LegacyHtmlChangelogExporter : LegacyReleaseNotesExporter
{
    protected override string Header(string version) =>
        "<h1>Changelog " + Escape(version) + "</h1>\n<ul>\n";

    protected override bool Include(ChangeEntry entry) => true;

    protected override string FormatEntry(ChangeEntry entry) =>
        "<li>[" + entry.Kind.ToString().ToUpperInvariant() + "] " + Escape(entry.Description) + "</li>\n";

    protected override string Footer() => "</ul>\n";

    private static string Escape(string text) =>
        text.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");
}
