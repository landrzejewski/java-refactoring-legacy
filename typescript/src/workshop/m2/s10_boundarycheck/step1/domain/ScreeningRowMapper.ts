import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { ScreeningRow } from '../adapter/ScreeningRow.js';
import { Screening } from './Screening.js';

/**
 * Krok 1: Extract Class - mapowanie na wiersz jako osobna, spójna klasa (LCOM4 = 1).
 * Wciąż leży w domain, więc test granicy nadal jest czerwony: spójność poprawiona,
 * kierunek zależności jeszcze nie.
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
