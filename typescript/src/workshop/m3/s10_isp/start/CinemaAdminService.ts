import type { Decimal } from 'decimal.js';

import type { LocalTime } from '../../../shared/time.js';

/**
 * Start: gruby interfejs "wszystkiego, co umie zaplecze kina". Każdy klient (kasa,
 * raport, tablica seansów) zależy od ośmiu metod, choć używa dwóch-trzech.
 * Zmiana sygnatury harmonogramu dotyka kasy (ponowne sprawdzenie typów i testy),
 * a fake w teście kasy musi implementować metody raportów i cennika.
 */
export interface CinemaAdminService {
  sellTicket(title: string, seat: number): string;

  refundTicket(ticketId: string): string;

  dailyRevenue(): Decimal;

  ticketsSold(title: string): number;

  scheduleScreening(title: string, start: LocalTime): void;

  cancelScreening(title: string): void;

  screenings(): string[];

  updateTicketPrice(price: Decimal): void;
}
