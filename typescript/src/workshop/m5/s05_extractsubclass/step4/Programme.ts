import { Screening } from './Screening.js';

/** Krok 4: bez zmian - klient przeżył całą ekstrakcję bez modyfikacji od kroku 1. */
export class Programme {
  line(title: string, format: string, guest: string | null): string {
    const screening: Screening = guest === null
      ? Screening.regular(title, format)
      : Screening.premiere(title, format, guest);
    return screening.describe() + ' | ' + screening.price();
  }
}
