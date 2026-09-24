import { IllegalStateError } from '../../../../shared/errors.js';
import type { Payments } from '../Payments.js';
import type { ReservationActions } from '../ReservationActions.js';
import { Status } from '../Status.js';

type Transition = (reservation: Reservation) => void;

interface ReservationState {
  readonly status: Status;
  readonly pay: Transition;
  readonly use: Transition;
}

/** Domyślne przejście rzuca wyjątek (nie jest puste!) - stan podaje tylko dozwolone przejścia. */
function state(status: Status, allowed: Partial<Omit<ReservationState, 'status'>> = {}): ReservationState {
  const invalid = (action: string): Transition => () => {
    throw new IllegalStateError(`cannot ${action} in ${status}`);
  };
  return { status, pay: invalid('pay'), use: invalid('use'), ...allowed };
}

/**
 * Krok 2: pay i use przeniesione do stanów. Kolejność: obciążenie, zmiana stanu, efekt.
 * Stany są statycznymi polami klasy, więc mają dostęp do jej prywatnych metod.
 */
export class Reservation implements ReservationActions {
  private static readonly NEW: ReservationState = state(Status.NEW, {
    pay: (reservation) => {
      reservation.charge();
      reservation.moveTo(Reservation.PAID);
      reservation.record('charged');
    },
  });

  private static readonly PAID: ReservationState = state(Status.PAID, {
    use: (reservation) => {
      reservation.moveTo(Reservation.USED);
      reservation.record('gate opened');
    },
  });

  // Stany końcowe - żadne przejście nie jest dozwolone.
  private static readonly USED = state(Status.USED);
  private static readonly EXPIRED = state(Status.EXPIRED);
  private static readonly CANCELLED = state(Status.CANCELLED);

  private readonly effectList: string[] = [];
  private state = Reservation.NEW;

  constructor(private readonly id: string, private readonly payments: Payments) {}

  pay(): void {
    this.state.pay(this);
  }

  use(): void {
    this.state.use(this);
  }

  expire(): void {
    if (this.state.status !== Status.NEW) {
      throw new IllegalStateError(`cannot expire in ${this.state.status}`);
    }
    this.moveTo(Reservation.EXPIRED);
    this.record('seats released');
  }

  cancel(): void {
    if (this.state.status === Status.NEW) {
      this.moveTo(Reservation.CANCELLED);
      this.record('seats released');
    } else if (this.state.status === Status.PAID) {
      this.moveTo(Reservation.CANCELLED);
      this.record('refund');
      this.record('seats released');
    } else {
      throw new IllegalStateError(`cannot cancel in ${this.state.status}`);
    }
  }

  status(): Status {
    return this.state.status;
  }

  effects(): readonly string[] {
    return Object.freeze([...this.effectList]);
  }

  private charge(): void {
    this.payments.charge(this.id);
  }

  private moveTo(next: ReservationState): void {
    this.state = next;
  }

  private record(effect: string): void {
    this.effectList.push(effect);
  }
}
