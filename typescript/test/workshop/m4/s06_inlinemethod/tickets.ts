import { Ticket } from '../../../../src/workshop/m4/s06_inlinemethod/Ticket.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';

// Bilety wspólne dla testów sceny (w Javie: stałe S06EquivalenceTest).
export const IMAX_EVENING = new Ticket(3, LocalTime.of(20, 0));
export const MORNING_3D = new Ticket(2, LocalTime.of(10, 0));
export const NOON_2D = new Ticket(1, LocalTime.NOON);
