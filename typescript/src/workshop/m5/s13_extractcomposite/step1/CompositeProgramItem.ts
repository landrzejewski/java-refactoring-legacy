import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { ProgramItem } from './ProgramItem.js';

/** Krok 1: Extract Superclass - wspólna obsługa dzieci (pole, add, children) w jednym miejscu. */
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }

  children(): readonly ProgramItem[] {
    return Object.freeze([...this.childList]);
  }

  abstract minutes(): number;

  abstract describe(): string;
}
