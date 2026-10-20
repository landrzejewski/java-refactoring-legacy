namespace Training.Module5.TemplateToStrategy.Before;

public class LegacyMarkdownHighlightsExporter : LegacyReleaseNotesExporter
{
    protected override string Header(string version) => "# Highlights " + version + "\n";

    protected override bool Include(ChangeEntry entry) => entry.Kind != Kind.Fix;

    protected override string FormatEntry(ChangeEntry entry) =>
        "- [" + entry.Kind.ToString().ToUpperInvariant() + "] " + entry.Description + "\n";

    protected override string Footer() => "";
}
