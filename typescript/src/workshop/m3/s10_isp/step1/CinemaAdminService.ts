import type { Decimal } from 'decimal.js';

import type { SalesFigures } from './SalesFigures.js';
import type { ScreeningSchedule } from './ScreeningSchedule.js';
import type { TicketSales } from './TicketSales.js';

/**
 * Krok 1: Extract Interface trzy razy - po jednej roli na klienta. Gruby interfejs
 * na razie zostaje jako suma ról (nic poza klientami się nie psuje).
 * Klienci zależą już tylko od swojej roli.
 */
export interface CinemaAdminService extends TicketSales, SalesFigures, ScreeningSchedule {
  updateTicketPrice(price: Decimal): void;
}
