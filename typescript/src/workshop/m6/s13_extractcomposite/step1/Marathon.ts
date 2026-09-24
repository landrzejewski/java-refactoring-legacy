import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Krok 1: pole children, add i children() podciągnięte do CompositeProgramItem. */
export class Marathon extends CompositeProgramItem {
  constructor(private readonly name: string) {
    super();
  }

  override minutes(): number {
    const children = this.children();
    let total = 0;
    for (const child of children) {
      total += child.minutes();
    }
    return children.length === 0 ? 0 : total + 15 * (children.length - 1);
  }

  override describe(): string {
    return `Maraton ${this.name} (${this.minutes()} min) `
      + `[${this.children().map((child) => child.describe()).join(', ')}]`;
  }
}
