import type { ChangeEntry } from '../ChangeEntry.js';
import { LegacyReleaseNotesExporter } from './LegacyReleaseNotesExporter.js';

export class LegacyHtmlChangelogExporter extends LegacyReleaseNotesExporter {
  protected override header(version: string): string {
    return '<h1>Changelog ' + LegacyHtmlChangelogExporter.escape(version) + '</h1>\n<ul>\n';
  }

  protected override include(_entry: ChangeEntry): boolean {
    return true;
  }

  protected override formatEntry(entry: ChangeEntry): string {
    return '<li>[' + entry.kind + '] ' + LegacyHtmlChangelogExporter.escape(entry.description) + '</li>\n';
  }

  protected override footer(): string {
    return '</ul>\n';
  }

  private static escape(text: string): string {
    return text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
