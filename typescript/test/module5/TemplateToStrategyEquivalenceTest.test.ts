import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../src/shared/errors.js';
import { ChangeEntry, Kind } from '../../src/module5/templatetostrategy/ChangeEntry.js';
import { ContentSelection } from '../../src/module5/templatetostrategy/after/ContentSelection.js';
import { HtmlFormat } from '../../src/module5/templatetostrategy/after/HtmlFormat.js';
import { MarkdownFormat } from '../../src/module5/templatetostrategy/after/MarkdownFormat.js';
import { ReleaseNotesExporter } from '../../src/module5/templatetostrategy/after/ReleaseNotesExporter.js';
import { LegacyHtmlChangelogExporter } from '../../src/module5/templatetostrategy/before/LegacyHtmlChangelogExporter.js';
import { LegacyHtmlHighlightsExporter } from '../../src/module5/templatetostrategy/before/LegacyHtmlHighlightsExporter.js';
import { LegacyMarkdownChangelogExporter } from '../../src/module5/templatetostrategy/before/LegacyMarkdownChangelogExporter.js';
import { LegacyMarkdownHighlightsExporter } from '../../src/module5/templatetostrategy/before/LegacyMarkdownHighlightsExporter.js';
import type { LegacyReleaseNotesExporter } from '../../src/module5/templatetostrategy/before/LegacyReleaseNotesExporter.js';

const ENTRIES: readonly ChangeEntry[] = Object.freeze([
  new ChangeEntry(Kind.FEATURE, 'Blue-green deployments'),
  new ChangeEntry(Kind.FIX, 'Retry <flaky> health check'),
  new ChangeEntry(Kind.BREAKING, 'Drop v1 API & legacy tokens'),
]);

const combinations: [string, LegacyReleaseNotesExporter, ReleaseNotesExporter][] = [
  ['markdown changelog', new LegacyMarkdownChangelogExporter(),
    new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.CHANGELOG)],
  ['markdown highlights', new LegacyMarkdownHighlightsExporter(),
    new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.HIGHLIGHTS)],
  ['html changelog', new LegacyHtmlChangelogExporter(),
    new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.CHANGELOG)],
  ['html highlights', new LegacyHtmlHighlightsExporter(),
    new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.HIGHLIGHTS)],
];

describe('TemplateToStrategyEquivalenceTest', () => {
  it.each(combinations)('everySubclassCombinationHasAnEquivalentComposition(%s)', (_name, before, after) => {
    expect(after.export('1.4.0', ENTRIES)).toBe(before.export('1.4.0', ENTRIES));
    expect(after.export('1.4.1', [])).toBe(before.export('1.4.1', []));
  });

  it('htmlHighlightsKeepEscapingAndFiltering', () => {
    expect(new ReleaseNotesExporter(new HtmlFormat(), ContentSelection.HIGHLIGHTS).export('1.4.0', ENTRIES)).toBe(
      '<h1>Highlights 1.4.0</h1>\n<ul>\n'
        + '<li>[FEATURE] Blue-green deployments</li>\n'
        + '<li>[BREAKING] Drop v1 API &amp; legacy tokens</li>\n'
        + '</ul>\n',
    );
  });

  it('newCombinationNeedsNoNewSubclass', () => {
    const breakingOnly = new ContentSelection('Breaking changes', entry => entry.kind === Kind.BREAKING);

    expect(new ReleaseNotesExporter(new MarkdownFormat(), breakingOnly).export('1.4.0', ENTRIES)).toBe(
      '# Breaking changes 1.4.0\n- [BREAKING] Drop v1 API & legacy tokens\n',
    );
  });

  it('exporterIsClosedForInheritance', () => {
    // Java sprawdza Modifier.isFinal(ReleaseNotesExporter.class). TS nie ma klas `final`,
    // więc konstruktor odrzuca każdą podklasę (new.target), a sama klasa działa normalnie.
    class CustomExporter extends ReleaseNotesExporter {}

    expect(() => new CustomExporter(new MarkdownFormat(), ContentSelection.CHANGELOG)).toThrow(IllegalStateError);
    expect(() => new ReleaseNotesExporter(new MarkdownFormat(), ContentSelection.CHANGELOG)).not.toThrow();
  });
});
