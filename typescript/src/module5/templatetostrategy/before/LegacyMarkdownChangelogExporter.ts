import type { ChangeEntry } from '../ChangeEntry.js';
import { LegacyReleaseNotesExporter } from './LegacyReleaseNotesExporter.js';

export class LegacyMarkdownChangelogExporter extends LegacyReleaseNotesExporter {
  protected override header(version: string): string {
    return '# Changelog ' + version + '\n';
  }

  protected override include(_entry: ChangeEntry): boolean {
    return true;
  }

  protected override formatEntry(entry: ChangeEntry): string {
    return '- [' + entry.kind + '] ' + entry.description + '\n';
  }

  protected override footer(): string {
    return '';
  }
}
