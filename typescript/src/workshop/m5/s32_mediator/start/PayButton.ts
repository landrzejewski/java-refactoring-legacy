import type { Money } from '../../../shared/Money.js';
import type { Basket } from './Basket.js';
import type { PriceSummary } from './PriceSummary.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Start: przycisk płatności - aktywny dla 1-4 miejsc. Po naciśnięciu sam pyta koszyk i sumę,
 * oznacza miejsca na mapie jako sprzedane i czyści koszyk, co uruchamia kaskadę odświeżeń.
 */
export class PayButton {
  private static readonly MAX_SEATS = 4;

  private enabled = false;
  private label = 'Wybierz miejsca';
  // uzupełniane przez connect() - wiązanie "każdy z każdym" po utworzeniu komponentów
  private seatMap!: SeatMap;
  private basket!: Basket;
  private summary!: PriceSummary;

  connect(seatMap: SeatMap, basket: Basket, summary: PriceSummary): void {
    this.seatMap = seatMap;
    this.basket = basket;
    this.summary = summary;
  }

  /** Akcja użytkownika: płatność za zawartość koszyka. */
  press(): string {
    if (!this.enabled) {
      return `Nieaktywny: ${this.label}`;
    }
    const paid = this.basket.seats();
    const amount = this.summary.total();
    this.seatMap.markSold(paid);
    this.basket.clear();
    return `Zaplacono ${amount.toString()} za ${paid.join(',')}`;
  }

  refresh(seats: number, total: Money): void {
    this.enabled = seats > 0 && seats <= PayButton.MAX_SEATS;
    if (seats === 0) {
      this.label = 'Wybierz miejsca';
    } else if (seats > PayButton.MAX_SEATS) {
      this.label = `Maksymalnie ${PayButton.MAX_SEATS} miejsca`;
    } else {
      this.label = `Zaplac ${total.toString()}`;
    }
  }

  describe(): string {
    return `przycisk=${this.label}`;
  }
}
