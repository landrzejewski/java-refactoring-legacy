import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Krok 1: pole children, add i children() podciągnięte do CompositeProgramItem. */
export class ShortsBlock extends CompositeProgramItem {
  constructor(private readonly name: string) {
    super();
  }

  override minutes(): number {
    let total = 0;
    for (const child of this.children()) {
      total += child.minutes();
    }
    return total;
  }

  override describe(): string {
    return `Blok ${this.name} (${this.minutes()} min) `
      + `[${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
