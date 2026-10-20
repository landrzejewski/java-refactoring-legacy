import { type ChangeEntry, Kind } from '../ChangeEntry.js';
import { LegacyReleaseNotesExporter } from './LegacyReleaseNotesExporter.js';

export class LegacyMarkdownHighlightsExporter extends LegacyReleaseNotesExporter {
  protected override header(version: string): string {
    return '# Highlights ' + version + '\n';
  }

  protected override include(entry: ChangeEntry): boolean {
    return entry.kind !== Kind.FIX;
  }

  protected override formatEntry(entry: ChangeEntry): string {
    return '- [' + entry.kind + '] ' + entry.description + '\n';
  }

  protected override footer(): string {
    return '';
  }
}
