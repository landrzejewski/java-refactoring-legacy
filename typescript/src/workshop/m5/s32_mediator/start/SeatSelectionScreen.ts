import { Basket } from './Basket.js';
import { PayButton } from './PayButton.js';
import { PriceSummary } from './PriceSummary.js';
import { SeatMap } from './SeatMap.js';

/**
 * Stabilny punkt wejścia sceny: ekran wyboru miejsc. Start: tylko tworzy komponenty
 * i łączy je "każdy z każdym" - 11 referencji między 4 komponentami.
 * (Akcesory Javy seatMap(), basket(), priceSummary(), payButton() to pola tylko do odczytu.)
 */
export class SeatSelectionScreen {
  readonly seatMap = new SeatMap();
  readonly basket = new Basket();
  readonly priceSummary = new PriceSummary();
  readonly payButton = new PayButton();

  constructor() {
    this.seatMap.connect(this.basket, this.priceSummary, this.payButton);
    this.basket.connect(this.seatMap, this.priceSummary, this.payButton);
    this.priceSummary.connect(this.basket, this.payButton);
    this.payButton.connect(this.seatMap, this.basket, this.priceSummary);
  }

  render(): string {
    return `${this.seatMap.describe()} ${this.basket.describe()} ${this.priceSummary.describe()} `
      + this.payButton.describe();
  }
}
