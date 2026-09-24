import { IllegalArgumentError } from '../../../../shared/errors.js';

/** Krok 1: Replace Type Code with Enum - zamknięty, znany kompilatorowi zbiór formatów. */
export enum Format {
  TWO_D = '2D',
  THREE_D = '3D',
  IMAX = 'IMAX',
}

export function parseFormat(code: string): Format {
  for (const format of Object.values(Format)) {
    if (format === code) {
      return format;
    }
  }
  throw new IllegalArgumentError(`nieznany format: ${code}`);
}
