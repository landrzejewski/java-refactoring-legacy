import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Krok 2: koszyk zna tylko mediatora - zgłasza usunięcie i wyczyszczenie, nie odświeża innych.
 * (W Javie add/drop/seats/describe są pakietowe; TypeScript nie ma tej widoczności.)
 */
export class Basket {
  private readonly items: string[] = [];
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: usuwa miejsce z koszyka (i odznacza je na mapie). */
  remove(seat: string): void {
    const index = this.items.indexOf(seat);
    if (index < 0) {
      return;
    }
    this.items.splice(index, 1);
    this.mediator.seatRemoved(seat);
  }

  /** Akcja użytkownika: czyści koszyk. Woła ją też mediator po płatności. */
  clear(): void {
    this.items.length = 0;
    this.mediator.basketCleared();
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
