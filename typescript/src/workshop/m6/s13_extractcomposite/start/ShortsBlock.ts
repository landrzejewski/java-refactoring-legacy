import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/** Start: drugi kontener z tą samą obsługą dzieci - filmy krótkie lecą bez przerw. */
export class ShortsBlock implements ProgramItem {
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
    return total;
  }

  describe(): string {
    return `Blok ${this.name} (${this.minutes()} min) `
      + `[${this.childList.map((child) => child.describe()).join(', ')}]`;
  }
}
