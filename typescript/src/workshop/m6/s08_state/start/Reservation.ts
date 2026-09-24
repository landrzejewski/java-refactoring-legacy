import { IllegalStateError } from '../../../../shared/errors.js';
import type { Payments } from '../Payments.js';
import type { ReservationActions } from '../ReservationActions.js';
import { Status } from '../Status.js';

/**
 * Start: każda operacja sprawdza status warunkami i sama go zmienia. Reguły przejść
 * NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED są rozsiane po metodach.
 */
export class Reservation implements ReservationActions {
  private readonly effectList: string[] = [];
  private currentStatus = Status.NEW;

  constructor(private readonly id: string, private readonly payments: Payments) {}

  pay(): void {
    if (this.currentStatus !== Status.NEW) {
      throw new IllegalStateError(`cannot pay in ${this.currentStatus}`);
    }
    this.payments.charge(this.id);
    this.currentStatus = Status.PAID;
    this.effectList.push('charged');
  }

  use(): void {
    if (this.currentStatus !== Status.PAID) {
      throw new IllegalStateError(`cannot use in ${this.currentStatus}`);
    }
    this.currentStatus = Status.USED;
    this.effectList.push('gate opened');
  }

  expire(): void {
    if (this.currentStatus !== Status.NEW) {
      throw new IllegalStateError(`cannot expire in ${this.currentStatus}`);
    }
    this.currentStatus = Status.EXPIRED;
    this.effectList.push('seats released');
  }

  cancel(): void {
    if (this.currentStatus === Status.NEW) {
      this.currentStatus = Status.CANCELLED;
      this.effectList.push('seats released');
    } else if (this.currentStatus === Status.PAID) {
      this.currentStatus = Status.CANCELLED;
      this.effectList.push('refund');
      this.effectList.push('seats released');
    } else {
      throw new IllegalStateError(`cannot cancel in ${this.currentStatus}`);
    }
  }

  status(): Status {
    return this.currentStatus;
  }

  effects(): readonly string[] {
    return Object.freeze([...this.effectList]);
  }
}
