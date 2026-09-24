import { describe, expect, it } from 'vitest';

import { Ticket } from '../../../../src/workshop/m4/s05_inlinevariable/Ticket.js';
import { type Clock, Duration } from '../../../../src/workshop/shared/time.js';
import { T0, TickingClock } from './TickingClock.js';

/** Kopia start po trzech naiwnych Inline Variable. */
class NaivelyInlinedIssuer {
  private lastNumber = 0;

  constructor(private readonly clock: Clock) {}

  issue(screeningCode: string, format: number): Ticket {
    return new Ticket(screeningCode + '-' + this.nextNumber(),
      'Bilet ' + screeningCode + '-' + this.nextNumber() + ', cena ' + money(basePrice(format))
        + ', oplata ' + money(200n),
      this.clock.now(), this.clock.now().plusMinutes(15));
  }

  private nextNumber(): number {
    this.lastNumber++;
    return this.lastNumber;
  }
}

function basePrice(format: number): bigint {
  return format === 3 ? 40n : format === 2 ? 32n : 25n;
}

function money(zloty: number): string;
function money(grosze: bigint): string;
function money(amount: number | bigint): string {
  if (typeof amount === 'bigint') {
    return `${amount / 100n}.${String(amount % 100n).padStart(2, '0')}`;
  }
  return amount.toFixed(2);
}

/**
 * Dokumentuje pułapkę: tak wyglądałby start po naiwnym Inline Variable dla number, issuedAt i price
 * (przy price wklejone bez konwersji Number(...), którą niosła zmienna).
 * Kod się kompiluje, IDE nie protestuje - zmienia się liczba i moment ewaluacji oraz przeciążenie.
 */
describe('S05InlineTrapTest', () => {
  const ticket = new NaivelyInlinedIssuer(new TickingClock(T0)).issue('D1', 3);

  it('inliningASideEffectConsumesTwoNumbers', () => {
    expect(ticket.code).toBe('D1-1');
    // Etykieta ma inny numer niż kod.
    expect(ticket.label).toBe('Bilet D1-2, cena 0.40, oplata 2.00');
  });

  it('inliningAClockReadGivesTwoDifferentInstants', () => {
    // Rezerwacja trzyma sekundę za długo.
    expect(Duration.between(ticket.issuedAt, ticket.holdUntil).toSeconds()).toBe(15 * 60 + 1);
  });

  it('inliningADoubleVariablePicksTheIntOverload', () => {
    // 40 zł potraktowane jak 40 groszy.
    expect(ticket.label.substring(ticket.label.indexOf('cena ') + 5, ticket.label.indexOf(', oplata'))).toBe('0.40');
  });
});
