import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';
import { LegacyBookingFlow } from './LegacyBookingFlow.js';
import { NewBookingFlow } from './NewBookingFlow.js';

/**
 * Start: tryb shadow "jak dla kalkulatora" zastosowany do ścieżki z efektami ubocznymi.
 * Wynik jest z legacy, ale kandydat też obciąża kartę, zapisuje i wysyła mail -
 * klient dostaje dwa maile i dwa obciążenia. Porównujemy tylko zwracany tekst.
 */
export class ShadowBooking {
  private readonly legacy: LegacyBookingFlow;
  private readonly candidate: NewBookingFlow;
  private readonly divergenceList: string[] = [];

  constructor(infra: Infrastructure) {
    this.legacy = new LegacyBookingFlow(infra);
    this.candidate = new NewBookingFlow(infra);
  }

  book(request: BookingRequest): string {
    const result = this.legacy.book(request);
    try {
      const shadow = this.candidate.book(request);
      if (shadow !== result) {
        this.divergenceList.push('legacy: ' + result + ' | kandydat: ' + shadow);
      }
    } catch (failure) {
      this.divergenceList.push('kandydat: ' + (failure as Error).message);
    }
    return result;
  }

  divergences(): readonly string[] {
    return Object.freeze([...this.divergenceList]);
  }
}
