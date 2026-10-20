import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/**
 * Krok 2: Pull Up Method - suma czasu dzieci i szkielet opisu też w nadklasie. Podklasy
 * dostarczają tylko to, czym naprawdę się różnią: etykietę i regułę przerw.
 */
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  protected constructor(private readonly name: string) {}

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  protected childrenMinutes(): number {
    let total = 0;
    for (const child of this.childList) {
      total += child.minutes();
    }
    return total;
  }

  describe(): string {
    return `${this.label()} ${this.name} (${this.minutes()} min) `
      + `[${this.childList.map((child) => child.describe()).join(', ')}]`;
  }

  abstract minutes(): number;

  protected abstract label(): string;
}
