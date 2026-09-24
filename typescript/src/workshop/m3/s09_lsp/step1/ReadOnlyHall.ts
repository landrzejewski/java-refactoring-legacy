import { UnsupportedOperationError } from '../../../../shared/errors.js';
import { Hall } from './Hall.js';

/**
 * Krok 1: bez zmian - wciąż dziedziczy po Hall i wciąż łamie jego kontrakt.
 * Sala archiwalna - plan miejsc zamkniętego seansu, potrzebny tylko raportom.
 * Dziedziczy po Hall, "bo też ma plan miejsc", ale `reserve` rzuca
 * UnsupportedOperationError. Kontrakt Hall nie przewiduje odmowy, więc podtyp
 * wzmacnia warunek wstępny (do "nigdy") - kasa dostająca Hall wybucha w runtime.
 */
export class ReadOnlyHall extends Hall {
  constructor(capacity: number, taken: Iterable<number>) {
    super(capacity, taken);
  }

  override reserve(_seat: number): void {
    throw new UnsupportedOperationError('sala archiwalna - tylko do odczytu');
  }
}
