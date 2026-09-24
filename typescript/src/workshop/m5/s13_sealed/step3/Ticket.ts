import type { ChildTicket } from './ChildTicket.js';
import type { SeniorTicket } from './SeniorTicket.js';
import type { StandardTicket } from './StandardTicket.js';
import type { StudentTicket } from './StudentTicket.js';

/** Krok 3: nowy wariant ChildTicket dopisany do unii - kompilacja PriceCalculator od razu się wywraca. */
export type Ticket = StandardTicket | StudentTicket | SeniorTicket | ChildTicket;
