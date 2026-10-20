import { Money } from '../../../shared/Money.js';
import { MenuComponent } from './MenuComponent.js';

/** Krok 1: zarządzanie dziećmi tylko w węźle; describe() rozszerza opis o dzieci. */
export class Combo extends MenuComponent {
  private readonly comboName: string;
  private readonly childList: MenuComponent[] = [];

  constructor(name: string) {
    super();
    this.comboName = name;
  }

  override name(): string {
    return this.comboName;
  }

  override price(): Money {
    let total = Money.ZERO;
    for (const child of this.childList) {
      total = total.plus(child.price());
    }
    return total;
  }

  add(child: MenuComponent): void {
    this.childList.push(child);
  }

  children(): readonly MenuComponent[] {
    return Object.freeze([...this.childList]);
  }

  override describe(): string {
    if (this.childList.length === 0) {
      return super.describe();
    }
    return `${super.describe()} [${this.childList.map((child) => child.describe()).join(', ')}]`;
  }
}
