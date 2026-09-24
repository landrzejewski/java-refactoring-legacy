import { Money } from '../../../shared/Money.js';
import { MenuComponent } from './MenuComponent.js';

/** Krok 1: liść nie ma już metody add() - nie da się jej wywołać przez pomyłkę. */
export class Product extends MenuComponent {
  private readonly productName: string;
  private readonly productPrice: Money;

  constructor(name: string, price: string) {
    super();
    this.productName = name;
    this.productPrice = Money.of(price);
  }

  override name(): string {
    return this.productName;
  }

  override price(): Money {
    return this.productPrice;
  }
}
