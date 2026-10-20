import { readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Status } from '../../../../src/workshop/m5/s26_variability/Status.js';
import { BookCommand } from '../../../../src/workshop/m5/s26_variability/step3/BookCommand.js';
import * as bookingModule from '../../../../src/workshop/m5/s26_variability/step3/Booking.js';
import { BookingLedger } from '../../../../src/workshop/m5/s26_variability/step3/BookingLedger.js';
import { BoxOfficeDesk } from '../../../../src/workshop/m5/s26_variability/step3/BoxOfficeDesk.js';
import { CancelCommand } from '../../../../src/workshop/m5/s26_variability/step3/CancelCommand.js';
import type { DeskCommand } from '../../../../src/workshop/m5/s26_variability/step3/DeskCommand.js';
import { PayCommand } from '../../../../src/workshop/m5/s26_variability/step3/PayCommand.js';
import { TicketPricings } from '../../../../src/workshop/m5/s26_variability/step3/TicketPricings.js';
import { UseCommand } from '../../../../src/workshop/m5/s26_variability/step3/UseCommand.js';
import { workshopDir } from '../../support/paths.js';

const { Booking } = bookingModule;
const STEP3 = (file: string) => readFileSync(workshopDir('m5', 's26_variability', 'step3', file), 'utf8');

// Każde polecenie z odbiorcą w stanie, w którym jego wykonanie jest dozwolone.
const COMMANDS = [
  { name: 'BookCommand', from: BookCommand.from, args: ['BOOK', 'B1', '2D', 'N', 'S'], setup: [] },
  { name: 'PayCommand', from: PayCommand.from, args: ['PAY', 'B1'], setup: ['open'] },
  { name: 'UseCommand', from: UseCommand.from, args: ['USE', 'B1'], setup: ['open', 'pay'] },
  { name: 'CancelCommand', from: CancelCommand.from, args: ['CANCEL', 'B1'], setup: ['open', 'pay'] },
] as const;

function ledgerFor(setup: readonly ('open' | 'pay')[]): BookingLedger {
  const ledger = new BookingLedger();
  for (const action of setup) {
    if (action === 'open') {
      ledger.open('B1', Money.of('25.00'));
    } else {
      ledger.pay('B1');
    }
  }
  return ledger;
}

// TypeScript nie ma refleksji typów pól: odpowiednikiem getDeclaredFields() są wartości
// własnych właściwości obiektu po wykonaniu operacji (pola prywatne TS to zwykłe właściwości).
function fieldValues(instance: object): unknown[] {
  return Object.values(instance);
}

// Strategia ceny to obiekt z metodą price(Money) - tak rozpoznajemy ją w polach.
function isPricing(value: unknown): boolean {
  return typeof value === 'object' && value !== null && typeof Reflect.get(value, 'price') === 'function';
}

function isStatus(value: unknown): boolean {
  return (Object.values(Status) as unknown[]).includes(value);
}

/** Trzy mechanizmy istnieją i każdy obsługuje jedną oś zmienności - osie się nie mieszają. */
describe('S26SolutionTest', () => {
  it('ticketPriceIsAStatelessStrategyThatKnowsOnlyMoney', () => {
    // Odpowiednik @FunctionalInterface: interfejs z dokładnie jedną metodą price(Money): Money.
    const pricing = STEP3('TicketPricing.ts');
    const body = /export interface TicketPricing \{([^}]*)\}/.exec(pricing)?.[1] ?? '';
    expect(body.trim()).toBe('price(formatBase: Money): Money;');
    expect(TicketPricings.forType('S')).toBe(TicketPricings.forType('S'));
    expect(TicketPricings.forType('S').price(Money.of('40.00'))).toEqual(Money.of('30.00'));
  });

  it('everyOperatorOperationIsACommandWithUndo', () => {
    // Interfejs DeskCommand nie istnieje w czasie wykonania: sprawdzamy "implements" w kodzie
    // i obecność metod kontraktu na każdym poleceniu.
    for (const command of COMMANDS) {
      expect(STEP3(`${command.name}.ts`), command.name).toMatch(
        new RegExp(`export class ${command.name} implements DeskCommand\\b`));
      const instance: DeskCommand = command.from(command.args);
      expect(typeof instance.execute, command.name).toBe('function');
      expect(typeof instance.undo, command.name).toBe('function');
      expect(typeof instance.journalEntry, command.name).toBe('function');
    }
    const contract = STEP3('DeskCommand.ts');
    expect(contract).toMatch(/^ {2}undo\(ledger: BookingLedger\): void;$/m);
    expect(contract).toMatch(/^ {2}journalEntry\(\): string;$/m);
  });

  it('lifecycleDecidesWhatCancelMeans', () => {
    const unpaid = new Booking(Money.of('40.00'));
    expect(unpaid.cancel()).toEqual(Money.ZERO);

    const paid = new Booking(Money.of('40.00'));
    expect(paid.pay()).toEqual(Money.of('40.00'));
    expect(paid.cancel(), 'w PAID anulowanie oddaje pieniądze').toEqual(Money.of('40.00'));
    expect(paid.status()).toBe(Status.CANCELLED);
    expect(() => paid.use()).toThrow(IllegalStateError);
    expect(() => paid.use()).toThrow('cannot use in CANCELLED');
  });

  it('stateLivesInsideBookingAsPrivateType', () => {
    // Odpowiednik prywatnego enuma: typ stanu i jego stałe nie wychodzą poza moduł Booking.
    expect(Object.keys(bookingModule), 'stany jako prywatny typ rezerwacji').toEqual(['Booking']);
    const source = STEP3('Booking.ts');
    expect(source).toMatch(/^interface BookingState\b/m);
    expect(source).toMatch(/^ {2}private static readonly NEW: BookingState\b/m);
    expect(source).toMatch(/^ {2}private state = Booking\.NEW;$/m);
  });

  it('axesAreNotMixed', () => {
    const booking = new Booking(Money.of('40.00'));
    booking.pay();
    expect(fieldValues(booking).some(isPricing), 'stan nie zna cennika').toBe(false);
    expect(fieldValues(booking).some((value) => typeof value === 'string'), 'stan nie zna nazw operacji').toBe(false);

    for (const command of COMMANDS) {
      const instance = command.from(command.args);
      instance.execute(ledgerFor(command.setup));
      const values = fieldValues(instance);
      expect(values.some(isPricing), `${command.name} nie trzyma strategii`).toBe(false);
      expect(values.some((value) => value instanceof Booking), `${command.name} nie sięga do stanu rezerwacji`)
        .toBe(false);
    }

    const desk = new BoxOfficeDesk();
    ['BOOK B1 2D N', 'PAY B1', 'REPORT'].forEach((line) => desk.handle(line));
    const deskValues = fieldValues(desk);
    expect(deskValues.some((value) => value instanceof Money || isStatus(value) || value instanceof Booking),
      `pulpit zna tylko polecenia i odbiorcę: ${deskValues.map((value) => String(value)).join(', ')}`).toBe(false);
  });
});
