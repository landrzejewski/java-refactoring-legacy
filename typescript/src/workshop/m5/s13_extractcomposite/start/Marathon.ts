import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/**
 * Start: kontener filmów. Obsługa dzieci (lista, add, children, suma, opis) jest skopiowana
 * w ShortsBlock. Różni się tylko reguła czasu: 15 minut przerwy między pozycjami.
 */
export class Marathon implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  constructor(private readonly name: string) {}

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  minutes(): number {
    let total = 0;
    for (const child of this.childList) {
      total += child.minutes();
    }
    return this.childList.length === 0 ? 0 : total + 15 * (this.childList.length - 1);
  }

  describe(): string {
    return `Maraton ${this.name} (${this.minutes()} min) `
      + `[${this.childList.map((child) => child.describe()).join(', ')}]`;
  }
}
