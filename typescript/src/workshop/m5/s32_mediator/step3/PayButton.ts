import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Krok 3: przycisk zna tylko mediatora - pilnuje własnej reguły (aktywny dla 1-4 miejsc)
 * i zgłasza chęć płatności. Dane o koszyku i kwocie zbiera mediator.
 */
export class PayButton {
  private static readonly MAX_SEATS = 4;

  private enabled = false;
  private label = 'Wybierz miejsca';
  private readonly mediator: SelectionMediator;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  /** Akcja użytkownika: płatność za zawartość koszyka. */
  press(): string {
    if (!this.enabled) {
      return `Nieaktywny: ${this.label}`;
    }
    return this.mediator.payRequested();
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
