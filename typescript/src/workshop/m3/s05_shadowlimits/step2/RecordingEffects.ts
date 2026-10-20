import type { Money } from '../../../shared/Money.js';
import type { Effects } from './Effects.js';

/**
 * Krok 2: przechwycenie efektów - nagrywa zamiary kandydata w formacie dziennika infrastruktury,
 * niczego nie wysyła, nie obciąża i nie zapisuje.
 */
export class RecordingEffects implements Effects {
  private readonly recordedList: string[] = [];

  sendMail(to: string, text: string): void {
    this.recordedList.push(`MAIL ${to}: ${text}`);
  }

  charge(card: string, amount: Money): void {
    this.recordedList.push(`CHARGE ${card}: ${amount.toString()}`);
  }

  save(row: string): void {
    this.recordedList.push(`SAVE ${row}`);
  }

  recorded(): readonly string[] {
    return Object.freeze([...this.recordedList]);
  }
}
