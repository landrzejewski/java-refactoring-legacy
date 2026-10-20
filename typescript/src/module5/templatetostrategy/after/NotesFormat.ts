import type { ChangeEntry } from '../ChangeEntry.js';

/**
 * Strategia formatu - dawne kroki header/formatEntry/footer szablonu.
 */
export interface NotesFormat {
  header(title: string, version: string): string;

  entry(entry: ChangeEntry): string;

  footer(): string;
}
