import { Basket } from './Basket.js';
import { PayButton } from './PayButton.js';
import { PriceSummary } from './PriceSummary.js';
import { SeatMap } from './SeatMap.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Stabilny punkt wejścia sceny. Krok 2: ekran-mediator obsługuje wszystkie zdarzenia, a kolejność
 * odświeżania (suma, potem przycisk) jest w jednej metodzie refresh() zamiast w czterech kopiach.
 * Zostało jedno połączenie wprost: przycisk czyta koszyk i sumę.
 */
export class SeatSelectionScreen implements SelectionMediator {
  readonly seatMap = new SeatMap(this);
  readonly basket = new Basket(this);
  readonly priceSummary = new PriceSummary(this);
  readonly payButton = new PayButton(this);

  constructor() {
    this.payButton.connect(this.basket, this.priceSummary);
  }

  seatToggled(seat: string, selected: boolean): void {
    if (selected) {
      this.basket.add(seat);
    } else {
      this.basket.drop(seat);
    }
    this.refresh();
  }

  seatRemoved(seat: string): void {
    this.seatMap.deselect(seat);
    this.refresh();
  }

  basketCleared(): void {
    this.seatMap.deselectAll();
    this.refresh();
  }

  voucherApplied(): void {
    this.refresh();
  }

  seatsPaid(seats: readonly string[]): void {
    this.seatMap.markSold(seats);
    this.basket.clear();
  }

  /** Jedno miejsce, które zna kolejność odświeżania: najpierw suma, potem przycisk. */
  private refresh(): void {
    this.priceSummary.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.priceSummary.total());
  }

  render(): string {
    return `${this.seatMap.describe()} ${this.basket.describe()} ${this.priceSummary.describe()} `
      + this.payButton.describe();
  }
}
