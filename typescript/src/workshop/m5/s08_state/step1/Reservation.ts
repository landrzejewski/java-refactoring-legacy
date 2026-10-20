import { IllegalStateError } from '../../../../shared/errors.js';
import type { Payments } from '../Payments.js';
import type { ReservationActions } from '../ReservationActions.js';
import { Status } from '../Status.js';

/** Obiekt stanu - na razie zna tylko swój Status. */
interface ReservationState {
  readonly status: Status;
}

/**
 * Krok 1: pole status zastąpione obiektem stanu. Warunki bez zmian - porównują
 * state.status. Efekty i zmiana stanu w metodach pomocniczych.
 */
export class Reservation implements ReservationActions {
  private static readonly NEW: ReservationState = { status: Status.NEW };
  private static readonly PAID: ReservationState = { status: Status.PAID };
  // Stany końcowe - żadne przejście nie jest dozwolone.
  private static readonly USED: ReservationState = { status: Status.USED };
  private static readonly EXPIRED: ReservationState = { status: Status.EXPIRED };
  private static readonly CANCELLED: ReservationState = { status: Status.CANCELLED };

  private readonly effectList: string[] = [];
  private state = Reservation.NEW;

  constructor(private readonly id: string, private readonly payments: Payments) {}

  pay(): void {
    if (this.state.status !== Status.NEW) {
      throw new IllegalStateError(`cannot pay in ${this.state.status}`);
    }
    this.charge();
    this.moveTo(Reservation.PAID);
    this.record('charged');
  }

  use(): void {
    if (this.state.status !== Status.PAID) {
      throw new IllegalStateError(`cannot use in ${this.state.status}`);
    }
    this.moveTo(Reservation.USED);
    this.record('gate opened');
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
