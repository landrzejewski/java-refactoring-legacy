import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';
import { LegacyBookingFlow } from './LegacyBookingFlow.js';
import { NewBookingFlow } from './NewBookingFlow.js';
import { RealEffects } from './RealEffects.js';

/**
 * Krok 1: cień nadal podaje kandydatowi prawdziwe efekty - podwójne maile i obciążenia zostają.
 * Ale mamy już szew: w następnym kroku wystarczy podać inną implementację Effects.
 */
export class ShadowBooking {
  private readonly legacy: LegacyBookingFlow;
  private readonly candidate: NewBookingFlow;
  private readonly divergenceList: string[] = [];

  constructor(infra: Infrastructure) {
    this.legacy = new LegacyBookingFlow(infra);
    this.candidate = new NewBookingFlow(new RealEffects(infra));
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
