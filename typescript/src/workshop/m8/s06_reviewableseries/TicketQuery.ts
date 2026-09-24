import type { LocalDateTime } from '../../shared/time.js';

/**
 * Stabilny kontrakt sceny: pytanie o cenę jednego biletu.
 *
 * format: 2D, 3D albo IMAX
 * type: NORMAL, STUDENT, SENIOR, CHILD
 * row: rząd (10 i dalej to VIP)
 */
export class TicketQuery {
  constructor(
    readonly format: string,
    readonly type: string,
    readonly start: LocalDateTime,
    readonly row: number,
  ) {}

  toString(): string {
    return `TicketQuery[format=${this.format}, type=${this.type}, start=${this.start.toString()}, row=${this.row}]`;
  }
}
