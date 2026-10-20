import type { LocalTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: pytanie o cenę jednego biletu.
 *
 * format: 2D, 3D, IMAX (inne formaty legacy wycenia na 0.00)
 * type: NORMAL, STUDENT, SENIOR, CHILD
 * row: rząd miejsca (10 i dalej to VIP)
 */
export class TicketQuery {
  constructor(
    readonly format: string,
    readonly type: string,
    readonly start: LocalTime,
    readonly row: number,
  ) {}

  toString(): string {
    return `${this.format} ${this.type} ${this.start.toString()} rzad ${this.row}`;
  }
}
