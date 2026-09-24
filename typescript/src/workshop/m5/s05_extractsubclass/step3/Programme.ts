import { Screening } from './Screening.js';

/** Krok 3: bez zmian. */
export class Programme {
  line(title: string, format: string, guest: string | null): string {
    const screening: Screening = guest === null
      ? Screening.regular(title, format)
      : Screening.premiere(title, format, guest);
    return screening.describe() + ' | ' + screening.price();
  }
}
