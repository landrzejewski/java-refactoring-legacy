import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { BookingTable } from '../../../../src/workshop/m8/s12_expandcontract/BookingTable.js';
import * as start from '../../../../src/workshop/m8/s12_expandcontract/start/BookingRepository.js';
import * as step1 from '../../../../src/workshop/m8/s12_expandcontract/step1/BookingRepository.js';
import * as step2 from '../../../../src/workshop/m8/s12_expandcontract/step2/BookingRepository.js';
import * as step3 from '../../../../src/workshop/m8/s12_expandcontract/step3/BookingRepository.js';
import { BookingPayloadFormat } from '../../../../src/workshop/m8/s12_expandcontract/step4/BookingPayloadFormat.js';
import * as step4 from '../../../../src/workshop/m8/s12_expandcontract/step4/BookingRepository.js';
import { workshopDir } from '../../support/paths.js';
import { ANNA, JAN } from './S12Fixtures.js';

/** Dane, wycofanie i zamknięcie migracji formatu rezerwacji (expand and contract). */
describe('S12SolutionTest', () => {
  it('step1DualWriteKeepsRollbackToTheOldVersionSafe', () => {
    const table = new BookingTable();
    new step1.BookingRepository(table).save(ANNA);
    expect(table.get('B1')?.payload).toBe('v2|id=B1|email=anna@kino.pl|seats=A5 A10|total=84.00');
    const oldVersion = new start.BookingRepository(table);
    expect(oldVersion.find('B1'), 'wycofane wydanie czyta dane nowego').toEqual(ANNA);
  });

  it('step2ReadsNewFormatAndFallsBackForOldRows', () => {
    const table = new BookingTable();
    new start.BookingRepository(table).save(JAN);
    const repository = new step2.BookingRepository(table);
    repository.save(ANNA);
    expect(repository.find('B2'), 'stary wiersz - odczyt z fallbackiem').toEqual(JAN);
    expect(repository.find('B1')).toEqual(ANNA);
    expect(new start.BookingRepository(table).find('B1')).toEqual(ANNA);
  });

  it('step3BackfillIsIdempotentAndPreparesTheContract', () => {
    const table = new BookingTable();
    new start.BookingRepository(table).save(JAN);
    const contracted = new step4.BookingRepository(table);
    expect(contracted.find('B2'), 'contract przed backfillem gubi stare wiersze').toBeUndefined();

    const repository = new step3.BookingRepository(table);
    expect(repository.migrateAll()).toBe(1);
    expect(repository.migrateAll(), 'drugie uruchomienie niczego nie zmienia').toBe(0);
    expect(contracted.find('B2')).toEqual(JAN);
  });

  it('step4ClosesTheRollbackWindow', () => {
    const table = new BookingTable();
    new step4.BookingRepository(table).save(ANNA);
    expect(new start.BookingRepository(table).find('B1'),
      'po contract stara wersja nie widzi nowych danych - wycofanie kodu już nie wystarczy').toBeUndefined();
  });

  it('payloadFormatIsVersioned', () => {
    expect(() => BookingPayloadFormat.read('v3|id=B1')).toThrow(IllegalArgumentError);
  });

  it('step4HasNoReferenceToTheOldFormat', async () => {
    const step4Dir = workshopDir('m8', 's12_expandcontract', 'step4');
    const offenders = readdirSync(step4Dir)
      .filter((name) => /csv/i.test(readFileSync(path.join(step4Dir, name), 'utf8')));
    expect(offenders).toEqual([]);
    // Odpowiednik Class.forName: modułu CsvBookingFormat w kroku 4 nie da się już załadować.
    await expect(import(pathToFileURL(path.join(step4Dir, 'CsvBookingFormat.ts')).href)).rejects.toThrow();
  });
});
