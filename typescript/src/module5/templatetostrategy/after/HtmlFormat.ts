import type { ChangeEntry } from '../ChangeEntry.js';
import type { NotesFormat } from './NotesFormat.js';

export class HtmlFormat implements NotesFormat {
  header(title: string, version: string): string {
    return '<h1>' + title + ' ' + HtmlFormat.escape(version) + '</h1>\n<ul>\n';
  }

  entry(entry: ChangeEntry): string {
    return '<li>[' + entry.kind + '] ' + HtmlFormat.escape(entry.description) + '</li>\n';
  }

  footer(): string {
    return '</ul>\n';
  }

  private static escape(text: string): string {
    return text.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
  }
}
