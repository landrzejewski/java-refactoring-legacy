import { Money } from '../../../shared/Money.js';
import { MenuComponent } from './MenuComponent.js';

/** Start: liść dziedziczy add(), który zawsze rzuca wyjątek. */
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
