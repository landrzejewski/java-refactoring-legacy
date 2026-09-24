import { Booking } from '../../../../src/workshop/m8/s12_expandcontract/Booking.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

// Rezerwacje wspólne dla S12EquivalenceTest i S12SolutionTest (w Javie: stałe S12EquivalenceTest).
export const ANNA = new Booking('B1', 'anna@kino.pl', ['A5', 'A10'], Money.of('84.00'));
export const JAN = new Booking('B2', 'jan@kino.pl', ['C7'], Money.of('25.00'));
