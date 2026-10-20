package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pl.training.module5.templatetostrategy.ChangeEntry;
import pl.training.module5.templatetostrategy.ChangeEntry.Kind;
import pl.training.module5.templatetostrategy.after.ContentSelection;
import pl.training.module5.templatetostrategy.after.HtmlFormat;
import pl.training.module5.templatetostrategy.after.MarkdownFormat;
import pl.training.module5.templatetostrategy.after.ReleaseNotesExporter;
import pl.training.module5.templatetostrategy.before.LegacyHtmlChangelogExporter;
import pl.training.module5.templatetostrategy.before.LegacyHtmlHighlightsExporter;
import pl.training.module5.templatetostrategy.before.LegacyMarkdownChangelogExporter;
import pl.training.module5.templatetostrategy.before.LegacyMarkdownHighlightsExporter;
import pl.training.module5.templatetostrategy.before.LegacyReleaseNotesExporter;

final class TemplateToStrategyEquivalenceTest {
    private static final List<ChangeEntry> ENTRIES = List.of(
            new ChangeEntry(Kind.FEATURE, "Blue-green deployments"),
            new ChangeEntry(Kind.FIX, "Retry <flaky> health check"),
            new ChangeEntry(Kind.BREAKING, "Drop v1 API & legacy tokens"));

    static Stream<Arguments> combinations() {
        return Stream.of(
                Arguments.of(new LegacyMarkdownChangelogExporter(),
                        new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.CHANGELOG)),
                Arguments.of(new LegacyMarkdownHighlightsExporter(),
                        new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.HIGHLIGHTS)),
                Arguments.of(new LegacyHtmlChangelogExporter(),
                        new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.CHANGELOG)),
                Arguments.of(new LegacyHtmlHighlightsExporter(),
                        new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.HIGHLIGHTS)));
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void everySubclassCombinationHasAnEquivalentComposition(
            LegacyReleaseNotesExporter before, ReleaseNotesExporter after) {
        assertEquals(before.export("1.4.0", ENTRIES), after.export("1.4.0", ENTRIES));
        assertEquals(before.export("1.4.1", List.of()), after.export("1.4.1", List.of()));
    }

    @Test
    void htmlHighlightsKeepEscapingAndFiltering() {
        assertEquals(
                "<h1>Highlights 1.4.0</h1>\n<ul>\n"
                        + "<li>[FEATURE] Blue-green deployments</li>\n"
                        + "<li>[BREAKING] Drop v1 API &amp; legacy tokens</li>\n"
                        + "</ul>\n",
                new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.HIGHLIGHTS)
                        .export("1.4.0", ENTRIES));
    }

    @Test
    void newCombinationNeedsNoNewSubclass() {
        var breakingOnly = new ContentSelection(
                "Breaking changes", entry -> entry.kind() == Kind.BREAKING);

        assertEquals(
                "# Breaking changes 1.4.0\n- [BREAKING] Drop v1 API & legacy tokens\n",
                new ReleaseNotesExporter(new MarkdownFormat(), breakingOnly)
                        .export("1.4.0", ENTRIES));
    }

    @Test
    void exporterIsClosedForInheritance() {
        assertTrue(Modifier.isFinal(ReleaseNotesExporter.class.getModifiers()));
    }
}
