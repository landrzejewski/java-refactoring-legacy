import type { PayButton } from './PayButton.js';
import type { PriceSummary } from './PriceSummary.js';
import type { SeatMap } from './SeatMap.js';

/**
 * Start: koszyk (miejsca w kolejności wyboru). Usunięcie i czyszczenie same
 * odświeżają mapę, sumę i przycisk.
 * (W Javie connect/add/drop/seats/describe są pakietowe; TypeScript nie ma tej widoczności.)
 */
export class Basket {
  private readonly items: string[] = [];
  // uzupełniane przez connect() - wiązanie "każdy z każdym" po utworzeniu komponentów
  private seatMap!: SeatMap;
  private summary!: PriceSummary;
  private payButton!: PayButton;

  connect(seatMap: SeatMap, summary: PriceSummary, payButton: PayButton): void {
    this.seatMap = seatMap;
    this.summary = summary;
    this.payButton = payButton;
  }

  /** Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie). */
  remove(seat: string): void {
    const index = this.items.indexOf(seat);
    if (index < 0) {
      return;
    }
    this.items.splice(index, 1);
    this.seatMap.deselect(seat);
    this.summary.recalculate(this.items);
    this.payButton.refresh(this.items.length, this.summary.total());
  }

  /** Akcja użytkownika: czyści koszyk. Woła ją też przycisk po płatności. */
  clear(): void {
    this.items.length = 0;
    this.seatMap.deselectAll();
    this.summary.recalculate(this.items);
    this.payButton.refresh(this.items.length, this.summary.total());
  }

  add(seat: string): void {
    this.items.push(seat);
  }

  drop(seat: string): void {
    const index = this.items.indexOf(seat);
    if (index >= 0) {
      this.items.splice(index, 1);
    }
  }

  seats(): readonly string[] {
    return [...this.items];
  }

  describe(): string {
    return `koszyk=[${this.items.join(', ')}]`;
  }
}
