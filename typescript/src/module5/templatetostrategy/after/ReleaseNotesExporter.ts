import { IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ChangeEntry } from '../ChangeEntry.js';
import type { ContentSelection } from './ContentSelection.js';
import type { NotesFormat } from './NotesFormat.js';

/**
 * Replace Inheritance with Delegation (Template Method -> Strategy): szkielet algorytmu
 * zostaje w jednej finalnej klasie, a zmienne kroki dostarczają dwie niezależne
 * strategie. N formatów i M zakresów to N + M klas zamiast N x M podklas.
 */
export class ReleaseNotesExporter {
  private readonly format: NotesFormat;
  private readonly selection: ContentSelection;

  constructor(format: NotesFormat, selection: ContentSelection) {
    // W Javie klasa jest `final`. TS nie ma klas final, więc konstruktor odrzuca podklasy.
    if (new.target !== ReleaseNotesExporter) {
      throw new IllegalStateError('ReleaseNotesExporter is final');
    }
    this.format = requireNonNull(format, 'format');
    this.selection = requireNonNull(selection, 'selection');
  }

  export(version: string, entries: readonly ChangeEntry[]): string {
    let output = this.format.header(this.selection.title, version);
    for (const entry of entries) {
      if (this.selection.includes(entry)) {
        output += this.format.entry(entry);
      }
    }
    return output + this.format.footer();
  }
}
