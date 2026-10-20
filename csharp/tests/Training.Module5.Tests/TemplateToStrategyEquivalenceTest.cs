using Training.Module5.TemplateToStrategy;
using Training.Module5.TemplateToStrategy.After;
using Training.Module5.TemplateToStrategy.Before;

namespace Training.Module5.Tests;

public sealed class TemplateToStrategyEquivalenceTest
{
    private static readonly IReadOnlyList<ChangeEntry> Entries =
    [
        new ChangeEntry(Kind.Feature, "Blue-green deployments"),
        new ChangeEntry(Kind.Fix, "Retry <flaky> health check"),
        new ChangeEntry(Kind.Breaking, "Drop v1 API & legacy tokens")
    ];

    public static TheoryData<LegacyReleaseNotesExporter, ReleaseNotesExporter> Combinations() => new()
    {
        {
            new LegacyMarkdownChangelogExporter(),
            new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.Changelog)
        },
        {
            new LegacyMarkdownHighlightsExporter(),
            new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.Highlights)
        },
        {
            new LegacyHtmlChangelogExporter(),
            new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.Changelog)
        },
        {
            new LegacyHtmlHighlightsExporter(),
            new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.Highlights)
        }
    };

    [Theory]
    [MemberData(nameof(Combinations))]
    public void EverySubclassCombinationHasAnEquivalentComposition(
        LegacyReleaseNotesExporter before, ReleaseNotesExporter after)
    {
        Assert.Equal(before.Export("1.4.0", Entries), after.Export("1.4.0", Entries));
        Assert.Equal(before.Export("1.4.1", []), after.Export("1.4.1", []));
    }

    [Fact]
    public void HtmlHighlightsKeepEscapingAndFiltering()
    {
        Assert.Equal(
            "<h1>Highlights 1.4.0</h1>\n<ul>\n"
            + "<li>[FEATURE] Blue-green deployments</li>\n"
            + "<li>[BREAKING] Drop v1 API &amp; legacy tokens</li>\n"
            + "</ul>\n",
            new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.Highlights)
                .Export("1.4.0", Entries));
    }

    [Fact]
    public void NewCombinationNeedsNoNewSubclass()
    {
        var breakingOnly = new ContentSelection(
            "Breaking changes", entry => entry.Kind == Kind.Breaking);

        Assert.Equal(
            "# Breaking changes 1.4.0\n- [BREAKING] Drop v1 API & legacy tokens\n",
            new ReleaseNotesExporter(new MarkdownFormat(), breakingOnly)
                .Export("1.4.0", Entries));
    }

    [Fact]
    public void ExporterIsClosedForInheritance()
    {
        Assert.True(typeof(ReleaseNotesExporter).IsSealed);
    }
}
