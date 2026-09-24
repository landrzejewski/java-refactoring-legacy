import { Screening } from './Screening.js';

/** Krok 1: klient wybiera wariant przez nazwę fabryki, a nie przez flagę i null. */
export class Programme {
  line(title: string, format: string, guest: string | null): string {
    const screening: Screening = guest === null
      ? Screening.regular(title, format)
      : Screening.premiere(title, format, guest);
    return screening.describe() + ' | ' + screening.price();
  }
}
