import type { Hall } from './Hall.js';

/**
 * Start: próg VIP to cecha SALI, a jest polem SEANSU. Każdy seans w tej samej sali niesie
 * własną kopię i nic nie pilnuje, żeby kopie były zgodne. SeatPricer czyta pole bezpośrednio.
 */
export class Screening {
  constructor(
    readonly hall: Hall,
    readonly format: number,
    readonly vipFromRow: number,
  ) {}

  isVip(row: number): boolean {
    return row >= this.vipFromRow;
  }
}
