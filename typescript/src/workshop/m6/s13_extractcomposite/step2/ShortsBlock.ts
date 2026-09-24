import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Krok 2: blok krótkich metraży = suma dzieci, bez przerw. */
export class ShortsBlock extends CompositeProgramItem {
  constructor(name: string) {
    super(name);
  }

  override minutes(): number {
    return this.childrenMinutes();
  }

  protected override label(): string {
    return 'Blok';
  }
}
