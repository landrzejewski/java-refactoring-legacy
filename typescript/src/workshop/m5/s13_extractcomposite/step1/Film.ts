import type { ProgramItem } from './ProgramItem.js';

/** Krok 1: bez zmian - liść - pojedynczy film. */
export class Film implements ProgramItem {
  constructor(readonly title: string, private readonly length: number) {}

  minutes(): number {
    return this.length;
  }

  describe(): string {
    return `${this.title} (${this.length} min)`;
  }
}
