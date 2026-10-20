import type { Basket } from './Basket.js';
import type { PayButton } from './PayButton.js';
import type { PriceSummary } from './PriceSummary.js';

/**
 * Start: mapa miejsc (sala A-C x 1-4, B2 sprzedane wcześniej). Po kliknięciu sama aktualizuje
 * koszyk, podsumowanie i przycisk - zna trzy inne komponenty i kolejność ich odświeżania.
 */
export class SeatMap {
  private readonly hall = SeatMap.hall();
  private readonly sold = new Set<string>(['B2']);
  private readonly selected = new Set<string>();
  // uzupełniane przez connect() - wiązanie "każdy z każdym" po utworzeniu komponentów
  private basket!: Basket;
  private summary!: PriceSummary;
  private payButton!: PayButton;

  connect(basket: Basket, summary: PriceSummary, payButton: PayButton): void {
    this.basket = basket;
    this.summary = summary;
    this.payButton = payButton;
  }

  /** Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane. */
  click(seat: string): void {
    if (!this.hall.has(seat) || this.sold.has(seat)) {
      return;
    }
    if (this.selected.delete(seat)) {
      this.basket.drop(seat);
    } else {
      this.selected.add(seat);
      this.basket.add(seat);
    }
    this.summary.recalculate(this.basket.seats());
    this.payButton.refresh(this.basket.seats().length, this.summary.total());
  }

  deselect(seat: string): void {
    this.selected.delete(seat);
  }

  deselectAll(): void {
    this.selected.clear();
  }

  markSold(seats: readonly string[]): void {
    seats.forEach((seat) => {
      this.sold.add(seat);
      this.selected.delete(seat);
    });
  }

  /** Zbiory wypisywane posortowane - jak TreeSet w Javie. */
  describe(): string {
    return `wybrane=${SeatMap.sorted(this.selected)} sprzedane=${SeatMap.sorted(this.sold)}`;
  }

  private static sorted(seats: ReadonlySet<string>): string {
    return `[${[...seats].sort().join(', ')}]`;
  }

  private static hall(): ReadonlySet<string> {
    const seats = new Set<string>();
    for (const row of ['A', 'B', 'C']) {
      for (let number = 1; number <= 4; number++) {
        seats.add(`${row}${number}`);
      }
    }
    return seats;
  }
}
