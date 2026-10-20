import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Money } from '../../../shared/Money.js';
import type { Basket } from './Basket.js';
import type { PriceSummary } from './PriceSummary.js';
import type { SelectionMediator } from './SelectionMediator.js';

/**
 * Krok 2: przycisk zgłasza płatność mediatorowi (to on oznacza miejsca i czyści koszyk),
 * ale nadal sam PYTA koszyk i podsumowanie o dane - zostały mu dwie bezpośrednie referencje.
 */
export class PayButton {
  private static readonly MAX_SEATS = 4;

  private enabled = false;
  private label = 'Wybierz miejsca';
  private readonly mediator: SelectionMediator;
  // uzupełniane przez connect() - jedyne połączenie wprost, które zostało
  private basket!: Basket;
  private summary!: PriceSummary;

  constructor(mediator: SelectionMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  connect(basket: Basket, summary: PriceSummary): void {
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
    this.mediator.seatsPaid(paid);
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
