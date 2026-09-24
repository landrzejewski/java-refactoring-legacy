import { Screening } from './Screening.js';

/** Krok 2: bez zmian - klient nie wie, że fabryka zwraca podklasę. */
export class Programme {
  line(title: string, format: string, guest: string | null): string {
    const screening: Screening = guest === null
      ? Screening.regular(title, format)
      : Screening.premiere(title, format, guest);
    return screening.describe() + ' | ' + screening.price();
  }
}
