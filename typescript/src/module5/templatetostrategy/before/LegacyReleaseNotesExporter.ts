import type { ChangeEntry } from '../ChangeEntry.js';

/**
 * Template Method z dwiema niezależnymi osiami (format i zakres treści) zaszytymi
 * w podklasach. Każda kombinacja to osobna klasa: 2 formaty x 2 zakresy = 4 podklasy,
 * a kolejny format dokłada następne dwie.
 */
export abstract class LegacyReleaseNotesExporter {
  // Metoda szablonowa. W Javie jest `final`; TS nie ma `final` dla metod,
  // więc podklasy po prostu jej nie nadpisują.
  export(version: string, entries: readonly ChangeEntry[]): string {
    let output = this.header(version);
    for (const entry of entries) {
      if (this.include(entry)) {
        output += this.formatEntry(entry);
      }
    }
    return output + this.footer();
  }

  protected abstract header(version: string): string;

  protected abstract include(entry: ChangeEntry): boolean;

  protected abstract formatEntry(entry: ChangeEntry): string;

  protected abstract footer(): string;
}
