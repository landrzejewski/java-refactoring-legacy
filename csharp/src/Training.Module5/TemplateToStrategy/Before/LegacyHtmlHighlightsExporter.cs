namespace Training.Module5.TemplateToStrategy.Before;

public class LegacyHtmlHighlightsExporter : LegacyReleaseNotesExporter
{
    protected override string Header(string version) =>
        "<h1>Highlights " + Escape(version) + "</h1>\n<ul>\n";

    protected override bool Include(ChangeEntry entry) => entry.Kind != Kind.Fix;

    protected override string FormatEntry(ChangeEntry entry) =>
        "<li>[" + entry.Kind.ToString().ToUpperInvariant() + "] " + Escape(entry.Description) + "</li>\n";

    protected override string Footer() => "</ul>\n";

    // skopiowane z LegacyHtmlChangelogExporter - poprawkę trzeba wprowadzać w dwóch miejscach
    private static string Escape(string text) =>
        text.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");
}
