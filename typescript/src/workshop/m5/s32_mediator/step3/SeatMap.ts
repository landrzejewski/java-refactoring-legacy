import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Krok 3: bez zmian od kroku 1 - mapa zna tylko mediatora. Zgłasza zmianę zaznaczenia i nie wie,
 * kto na nią reaguje. Wciąż udostępnia operacje, które wołają na niej inni (deselect, markSold).
 */
export class SeatMap {
  private readonly hall = SeatMap.hall();
  private readonly sold = new Set<string>(['B2']);
  private readonly selected = new Set<string>();
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: zaznacza albo odznacza miejsce; sprzedane i spoza sali są ignorowane. */
  click(seat: string): void {
    if (!this.hall.has(seat) || this.sold.has(seat)) {
      return;
    }
    const nowSelected = !this.selected.delete(seat);
    if (nowSelected) {
      this.selected.add(seat);
    }
    this.mediator.seatToggled(seat, nowSelected);
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
