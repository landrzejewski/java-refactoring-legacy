import { Basket } from './Basket.js';
import { PayButton } from './PayButton.js';
import { PriceSummary } from './PriceSummary.js';
import { SeatMap } from './SeatMap.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Stabilny punkt wejścia sceny. Krok 3: ekran jest jedynym miejscem, które zna wszystkie cztery
 * komponenty; każdy komponent zna tylko interfejs SelectionMediator (4 referencje zamiast 11).
 * Nowy komponent (np. licznik czasu rezerwacji) to zmiana w mediatorze, a nie w czterech klasach.
 */
export class SeatSelectionScreen implements SelectionMediator {
  readonly seatMap = new SeatMap(this);
  readonly basket = new Basket(this);
  readonly priceSummary = new PriceSummary(this);
  readonly payButton = new PayButton(this);

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

  payRequested(): string {
    const paid = this.basket.seats();
    const amount = this.priceSummary.total();
    this.seatMap.markSold(paid);
    this.basket.clear();
    return `Zaplacono ${amount.toString()} za ${paid.join(',')}`;
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
