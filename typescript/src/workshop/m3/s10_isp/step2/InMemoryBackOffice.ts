import { Decimal } from 'decimal.js';

import type { LocalTime } from '../../../shared/time.js';
import type { SalesFigures } from './SalesFigures.js';
import type { ScreeningSchedule } from './ScreeningSchedule.js';
import type { TicketSales } from './TicketSales.js';

/**
 * Krok 2 (rozwiązanie): gruby interfejs usunięty (Safe Delete po przepięciu użyć).
 * Implementacja realizuje trzy role klientów; zmiana ceny to zwykła metoda
 * klasy - jej jedynym klientem jest konfiguracja, więc nie potrzebuje interfejsu.
 */
export class InMemoryBackOffice implements TicketSales, SalesFigures, ScreeningSchedule {
  private readonly activeTickets = new Map<string, string>();
  // klucz "HH:mm" - kolejność alfabetyczna kluczy to kolejność godzin
  private readonly schedule = new Map<string, string>();
  private ticketPrice = new Decimal('25.00');
  private nextTicket = 1;

  sellTicket(title: string, _seat: number): string {
    const id = `T-${this.nextTicket++}`;
    this.activeTickets.set(id, title);
    return id;
  }

  refundTicket(ticketId: string): string {
    this.activeTickets.delete(ticketId);
    return `zwrot ${ticketId}`;
  }

  dailyRevenue(): Decimal {
    return this.ticketPrice.times(this.activeTickets.size);
  }

  ticketsSold(title: string): number {
    return [...this.activeTickets.values()].filter((sold) => sold === title).length;
  }

  scheduleScreening(title: string, start: LocalTime): void {
    this.schedule.set(start.toString(), title);
  }

  cancelScreening(title: string): void {
    const start = this.startTimes().find((time) => this.schedule.get(time) === title);
    if (start !== undefined) {
      this.schedule.delete(start);
    }
  }

  screenings(): string[] {
    return this.startTimes().map((start) => `${start} ${this.schedule.get(start)}`);
  }

  updateTicketPrice(price: Decimal): void {
    this.ticketPrice = price;
  }

  private startTimes(): string[] {
    return [...this.schedule.keys()].sort();
  }
}
