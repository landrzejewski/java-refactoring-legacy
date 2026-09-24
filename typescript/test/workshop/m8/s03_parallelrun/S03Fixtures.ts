import { TicketQuery } from '../../../../src/workshop/m8/s03_parallelrun/TicketQuery.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';

// Zapytania wspólne dla S03EquivalenceTest i S03SolutionTest (w Javie: stałe S03EquivalenceTest).
export const IMAX_NORMAL = new TicketQuery('IMAX', 'NORMAL', LocalTime.of(20, 0), 5);
export const IMAX_STUDENT_VIP = new TicketQuery('IMAX', 'STUDENT', LocalTime.of(20, 0), 10);
export const MORNING_3D_CHILD = new TicketQuery('3D', 'CHILD', LocalTime.of(10, 30), 3);
export const MORNING_3D_NORMAL_VIP = new TicketQuery('3D', 'NORMAL', LocalTime.of(10, 30), 12);
export const EVENING_2D_SENIOR = new TicketQuery('2D', 'SENIOR', LocalTime.of(18, 0), 1);
export const MORNING_2D_STUDENT = new TicketQuery('2D', 'STUDENT', LocalTime.of(9, 0), 2);
export const UNKNOWN_4DX = new TicketQuery('4DX', 'NORMAL', LocalTime.of(20, 0), 1);
