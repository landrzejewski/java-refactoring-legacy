import { Money } from '../../../shared/Money.js';
import { MenuComponent } from './MenuComponent.js';

/** Start: zestaw nadpisuje add() i children(). */
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

  override add(child: MenuComponent): void {
    this.childList.push(child);
  }

  override children(): readonly MenuComponent[] {
    return Object.freeze([...this.childList]);
  }
}
