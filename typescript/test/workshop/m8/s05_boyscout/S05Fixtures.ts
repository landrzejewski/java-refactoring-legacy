import { Ticket } from '../../../../src/workshop/m8/s05_boyscout/Ticket.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';

// Bilety wspólne dla S05EquivalenceTest i S05SolutionTest (w Javie: stałe S05EquivalenceTest).
export const REGULAR = new Ticket('Amator', LocalDateTime.of(2026, 3, 14, 18, 0),
  ['C5'], 'jan@kino.pl', '600100200', 25.00);
export const ROWS_9_AND_10 = new Ticket('Diuna', LocalDateTime.of(2026, 3, 13, 20, 0),
  ['A9', 'A10'], 'anna@kino.pl', '600100300', 84.00);
export const NO_PHONE = new Ticket('Kraina Lodu', LocalDateTime.of(2026, 3, 14, 10, 30),
  ['B1', 'B2'], 'ola@kino.pl', null, 47.20);
export const MIXED_CASE_EMAIL = new Ticket('Amator', LocalDateTime.of(2026, 3, 14, 18, 0),
  ['D7'], ' Anna@Kino.pl ', '600100400', 25.00);
