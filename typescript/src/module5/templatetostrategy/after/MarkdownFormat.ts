import type { ChangeEntry } from '../ChangeEntry.js';
import type { NotesFormat } from './NotesFormat.js';

export class MarkdownFormat implements NotesFormat {
  header(title: string, version: string): string {
    return '# ' + title + ' ' + version + '\n';
  }

  entry(entry: ChangeEntry): string {
    return '- [' + entry.kind + '] ' + entry.description + '\n';
  }

  footer(): string {
    return '';
  }
}
