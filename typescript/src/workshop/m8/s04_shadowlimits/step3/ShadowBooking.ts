import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';
import { BookingPlanner } from './BookingPlanner.js';
import { LegacyBookingFlow } from './LegacyBookingFlow.js';

/**
 * Krok 3: cień woła tylko czysty BookingPlanner - bez nagrywarek i bez ryzyka, że ktoś
 * podepnie prawdziwy adapter. Podwójne wykonanie dotyczy wyłącznie obliczenia.
 */
export class ShadowBooking {
  private readonly legacy: LegacyBookingFlow;
  private readonly candidate = new BookingPlanner();
  private readonly divergenceList: string[] = [];

  constructor(private readonly infra: Infrastructure) {
    this.legacy = new LegacyBookingFlow(infra);
  }

  book(request: BookingRequest): string {
    const before = this.infra.log().length;
    const result = this.legacy.book(request);
    const legacyEffects = this.infra.log().slice(before);
    try {
      const plan = this.candidate.plan(request);
      this.compare('wynik', [result], [plan.result]);
      const planned = plan.effects.map((effect) => effect.describe());
      this.compare('efekty', legacyEffects, planned);
    } catch (failure) {
      this.divergenceList.push('kandydat: ' + (failure as Error).message);
    }
    return result;
  }

  private compare(what: string, legacyValues: readonly string[], candidateValues: readonly string[]): void {
    for (let i = 0; i < Math.max(legacyValues.length, candidateValues.length); i++) {
      const legacyValue = i < legacyValues.length ? legacyValues[i]! : '-';
      const candidateValue = i < candidateValues.length ? candidateValues[i]! : '-';
      if (legacyValue !== candidateValue) {
        this.divergenceList.push(what + ' legacy: ' + legacyValue + ' | kandydat: ' + candidateValue);
      }
    }
  }

  divergences(): readonly string[] {
    return Object.freeze([...this.divergenceList]);
  }
}
