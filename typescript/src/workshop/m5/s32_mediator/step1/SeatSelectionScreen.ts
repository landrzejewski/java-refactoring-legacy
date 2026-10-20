import { Basket } from './Basket.js';
import { PayButton } from './PayButton.js';
import { PriceSummary } from './PriceSummary.js';
import { SeatMap } from './SeatMap.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Stabilny punkt wejścia sceny: ekran wyboru miejsc. Krok 1: Extract Interface
 * SelectionMediator + Move Method - ekran (jak "dialog" z GoF) przejmuje reakcję na kliknięcie
 * miejsca. Pozostałe komponenty nadal są połączone wprost. Mapa dostaje mediatora
 * w konstruktorze ("this" przed końcem konstrukcji - bezpieczne, bo mediator działa dopiero
 * po akcji użytkownika).
 */
export class SeatSelectionScreen implements SelectionMediator {
  readonly seatMap = new SeatMap(this);
  readonly basket = new Basket();
  readonly priceSummary = new PriceSummary();
  readonly payButton = new PayButton();

  constructor() {
    this.basket.connect(this.seatMap, this.priceSummary, this.payButton);
    this.priceSummary.connect(this.basket, this.payButton);
    this.payButton.connect(this.seatMap, this.basket, this.priceSummary);
  }

  seatToggled(seat: string, selected: boolean): void {
    if (selected) {
      this.basket.add(seat);
    } else {
      this.basket.drop(seat);
    }
    this.priceSummary.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.priceSummary.total());
  }

  render(): string {
    return `${this.seatMap.describe()} ${this.basket.describe()} ${this.priceSummary.describe()} `
      + this.payButton.describe();
  }
}
