import { type ChangeEntry, Kind } from '../ChangeEntry.js';
import { LegacyReleaseNotesExporter } from './LegacyReleaseNotesExporter.js';

export class LegacyHtmlHighlightsExporter extends LegacyReleaseNotesExporter {
  protected override header(version: string): string {
    return '<h1>Highlights ' + LegacyHtmlHighlightsExporter.escape(version) + '</h1>\n<ul>\n';
  }

  protected override include(entry: ChangeEntry): boolean {
    return entry.kind !== Kind.FIX;
  }

  protected override formatEntry(entry: ChangeEntry): string {
    return '<li>[' + entry.kind + '] ' + LegacyHtmlHighlightsExporter.escape(entry.description) + '</li>\n';
  }

  protected override footer(): string {
    return '</ul>\n';
  }

  // skopiowane z LegacyHtmlChangelogExporter - poprawkę trzeba wprowadzać w dwóch miejscach
  private static escape(text: string): string {
    return text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
