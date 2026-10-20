import type { FilmLicense } from './FilmLicense.js';

/** Krok 3: port domeny - skąd kino bierze licencje, opisane w języku kina (brak = undefined, nie null). */
export interface FilmLicenses {
  find(filmCode: string): FilmLicense | undefined;
}
