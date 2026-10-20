import type { Money } from '../../../shared/Money.js';
import type { Infrastructure } from '../Infrastructure.js';
import type { Effects } from './Effects.js';

/** Krok 3 (bez zmian): adapter portu na prawdziwą infrastrukturę. */
export class RealEffects implements Effects {
  constructor(private readonly infra: Infrastructure) {}

  sendMail(to: string, text: string): void {
    this.infra.sendMail(to, text);
  }

  charge(card: string, amount: Money): void {
    this.infra.charge(card, amount);
  }

  save(row: string): void {
    this.infra.save(row);
  }
}
