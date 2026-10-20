import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { Screening } from '../domain/Screening.js';
import { ScreeningRow } from './ScreeningRow.js';

/**
 * Krok 2 (rozwiązanie): Move Class do adapter. Zależność biegnie adapter -> domain,
 * domena nie zna sql ani wiersza tabeli. Test granicy zielony.
 */
export class ScreeningRowMapper {
  constructor(private readonly table: string) {}

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
