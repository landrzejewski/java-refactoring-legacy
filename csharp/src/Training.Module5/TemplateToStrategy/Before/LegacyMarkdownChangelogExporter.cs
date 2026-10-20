namespace Training.Module5.TemplateToStrategy.Before;

public class LegacyMarkdownChangelogExporter : LegacyReleaseNotesExporter
{
    protected override string Header(string version) => "# Changelog " + version + "\n";

    protected override bool Include(ChangeEntry entry) => true;

    protected override string FormatEntry(ChangeEntry entry) =>
        "- [" + entry.Kind.ToString().ToUpperInvariant() + "] " + entry.Description + "\n";

    protected override string Footer() => "";
}
