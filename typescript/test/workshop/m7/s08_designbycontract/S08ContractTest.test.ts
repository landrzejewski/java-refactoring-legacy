import { describe, expect, it } from 'vitest';

import { IllegalArgumentError, IllegalStateError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m7/s08_designbycontract/start/SeatPool.js';
import * as step1 from '../../../../src/workshop/m7/s08_designbycontract/step1/SeatPool.js';
import * as step2 from '../../../../src/workshop/m7/s08_designbycontract/step2/SeatPool.js';
import { Scene } from '../../support/scene.js';

/**
 * Dla poprawnych wejść start i kroki są równoważne. Dla niepoprawnych - świadomie NIE:
 * dodanie kontroli kontraktu to zmiana zachowania i test pokazuje ją jawnie.
 */
interface Pool {
  reserve(seats: number): boolean;
  release(seats: number): void;
  remaining(): number;
}

function runStart(ops: readonly string[]): string {
  return run(ops, new start.SeatPool(100));
}

function runStep1(ops: readonly string[]): string {
  return run(ops, new step1.SeatPool(100));
}

function runStep2(ops: readonly string[]): string {
  return run(ops, new step2.SeatPool(100));
}

/** Wykonuje operacje i zapisuje wektor: wynik albo wyjątek oraz stan po każdej operacji. */
function run(ops: readonly string[], pool: Pool): string {
  return ops.map((op) => {
    const [name, count = ''] = op.split(' ');
    const seats = Number.parseInt(count, 10);
    const withState = (outcome: string): string => `${op} -> ${outcome}zostalo ${pool.remaining()}`;
    try {
      if (name === 'reserve') {
        return withState(`${pool.reserve(seats)}, `);
      }
      pool.release(seats);
      return withState('');
    } catch (error) {
      if (error instanceof IllegalArgumentError || error instanceof IllegalStateError) {
        return withState(`${error.name}: ${error.message}, `);
      }
      throw error;
    }
  }).join('; ');
}

describe('S08ContractTest', () => {
  describe('validUsageBehavesTheSame', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', runStart)
      .variant('step1', runStep1)
      .variant('step2', runStep2)
      .expect('rezerwacja i zwolnienie', ['reserve 3', 'reserve 90', 'release 2'],
        'reserve 3 -> true, zostalo 97; reserve 90 -> true, zostalo 7; release 2 -> zostalo 9')
      .expect('za malo miejsc to false, nie wyjatek', ['reserve 98', 'reserve 5'],
        'reserve 98 -> true, zostalo 2; reserve 5 -> false, zostalo 2')
      .expect('cala sala i zwrot wszystkiego', ['reserve 100', 'release 100'],
        'reserve 100 -> true, zostalo 0; release 100 -> zostalo 100')
      .tests();
  });

  it('startSilentlyCorruptsStateForInvalidInput', () => {
    expect(runStart(['reserve -2'])).toBe('reserve -2 -> true, zostalo 102');
    expect(runStart(['reserve 10', 'release 15']))
      .toBe('reserve 10 -> true, zostalo 90; release 15 -> zostalo 105');
  });

  it('preconditionsRejectInvalidInputAndLeaveStateUntouched', () => {
    const negative = 'reserve -2 -> IllegalArgumentError: seats must be positive, zostalo 100';
    expect(runStep1(['reserve -2'])).toBe(negative);
    expect(runStep2(['reserve -2'])).toBe(negative);
    const tooMany = 'reserve 10 -> true, zostalo 90; '
      + 'release 15 -> IllegalArgumentError: cannot release more seats than reserved, zostalo 90';
    expect(runStep1(['reserve 10', 'release 15'])).toBe(tooMany);
    expect(runStep2(['reserve 10', 'release 15'])).toBe(tooMany);
  });
});
