import { Screening } from './Screening.js';

/** Start: klient importujący repertuar - guest === null oznacza zwykły seans. */
export class Programme {
  line(title: string, format: string, guest: string | null): string {
    const screening = new Screening(title, format, guest !== null, guest);
    return screening.describe() + ' | ' + screening.price();
  }
}
