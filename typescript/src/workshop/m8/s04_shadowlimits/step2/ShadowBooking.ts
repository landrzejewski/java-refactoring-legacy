import type { BookingRequest } from '../BookingRequest.js';
import type { Infrastructure } from '../Infrastructure.js';
import { LegacyBookingFlow } from './LegacyBookingFlow.js';
import { NewBookingFlow } from './NewBookingFlow.js';
import { RecordingEffects } from './RecordingEffects.js';

/**
 * Krok 2: w cieniu kandydat dostaje RecordingEffects. Klient dostaje jeden mail i jedno
 * obciążenie, a cień porównuje nie tylko wynik, ale też ZAMIERZONE efekty z tymi, które
 * faktycznie wykonało legacy - i znajduje różnicę w treści maila.
 */
export class ShadowBooking {
  private readonly legacy: LegacyBookingFlow;
  private readonly divergenceList: string[] = [];

  constructor(private readonly infra: Infrastructure) {
    this.legacy = new LegacyBookingFlow(infra);
  }

  book(request: BookingRequest): string {
    const before = this.infra.log().length;
    const result = this.legacy.book(request);
    const legacyEffects = this.infra.log().slice(before);
    try {
      const recorder = new RecordingEffects();
      const shadow = new NewBookingFlow(recorder).book(request);
      this.compare('wynik', [result], [shadow]);
      this.compare('efekty', legacyEffects, recorder.recorded());
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
