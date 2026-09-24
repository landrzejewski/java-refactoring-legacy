import type { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { ScreeningRow } from '../adapter/ScreeningRow.js';
import { Screening } from './Screening.js';

/**
 * Start: klasa domeny miesza politykę cenową z mapowaniem na wiersz bazy.
 * Naruszenie granicy: domain importuje adapter i sql. Diagnostyka spójności:
 * dwie grupy metod na rozłącznych polach (LCOM4 = 2) - dwa pojęcia w jednej klasie.
 */
export class ScreeningService {
  constructor(
    private readonly basePrice: Decimal,
    private readonly morningDiscount: Decimal,
    private readonly table: string,
  ) {}

  price(screening: Screening): Decimal {
    return this.isMorning(screening) ? this.basePrice.minus(this.morningDiscount) : this.basePrice;
  }

  private isMorning(screening: Screening): boolean {
    return screening.start.hour < 12;
  }

  toRow(screening: Screening): ScreeningRow {
    return new ScreeningRow(this.table, screening.title, Timestamp.valueOf(screening.start));
  }

  fromRow(row: ScreeningRow): Screening {
    if (row.table !== this.table) {
      throw new IllegalArgumentError(`obca tabela: ${row.table}`);
    }
    return new Screening(row.title, row.start.toLocalDateTime());
  }
}
