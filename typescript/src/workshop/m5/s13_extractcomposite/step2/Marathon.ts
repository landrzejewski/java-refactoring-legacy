import { CompositeProgramItem } from './CompositeProgramItem.js';

/** Krok 2: maraton = suma dzieci + 15 minut przerwy między pozycjami. */
export class Marathon extends CompositeProgramItem {
  constructor(name: string) {
    super(name);
  }

  override minutes(): number {
    const count = this.children().length;
    return count === 0 ? 0 : this.childrenMinutes() + 15 * (count - 1);
  }

  protected override label(): string {
    return 'Maraton';
  }
}
