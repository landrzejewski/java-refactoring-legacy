import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LegacyTerminalClient } from '../../../../src/workshop/m5/s29_boundaryobject/LegacyTerminalClient.js';
import * as start from '../../../../src/workshop/m5/s29_boundaryobject/start/PaymentModule.js';
import * as step1 from '../../../../src/workshop/m5/s29_boundaryobject/step1/PaymentModule.js';
import * as step2 from '../../../../src/workshop/m5/s29_boundaryobject/step2/PaymentModule.js';
import * as step3 from '../../../../src/workshop/m5/s29_boundaryobject/step3/PaymentModule.js';
import { Scene } from '../../support/scene.js';

interface Service {
  pay(bookingId: string, amount: Money): string;
  receipt(bookingId: string): string;
  totalAuthorized(): Money;
}

function console(service: Service): (line: string) => string {
  return (line) => {
    const parts = line.split(' ');
    switch (parts[0]) {
      case 'pay': return service.pay(parts[1] ?? '', Money.of(parts[2] ?? ''));
      case 'receipt': return service.receipt(parts[1] ?? '');
      case 'total': return service.totalAuthorized().toString();
      default: throw new IllegalArgumentError(line);
    }
  };
}

function session(service: () => Service): (lines: readonly string[]) => string {
  return (lines) => {
    const handle = console(service());
    return lines.map(handle).join('\n');
  };
}

/**
 * Sesja płatności: autoryzacja pełna, częściowa (granica 300.00), odmowa, błąd kwoty,
 * nadpisanie płatności, potwierdzenia i suma autoryzacji. Wynik identyczny niezależnie od tego,
 * gdzie i ile razy rozbieramy odpowiedź terminala.
 */
describe('S29EquivalenceTest', () => {
  describe('everyStepReadsTheTerminalTheSame', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', session(() => start.PaymentModule.paymentService(new LegacyTerminalClient())))
      .variant('step1', session(() => step1.PaymentModule.paymentService(new LegacyTerminalClient())))
      .variant('step2', session(() => step2.PaymentModule.paymentService(new LegacyTerminalClient())))
      .variant('step3', session(() => step3.PaymentModule.paymentService(new LegacyTerminalClient())))
      .expect('pełna autoryzacja', ['pay R1 45.00', 'receipt R1', 'total'], [
        'PAID AUTH-R1',
        'Platnosc R1: 45.00 (AUTH-R1)',
        '45.00',
      ].join('\n'))
      .expect('autoryzacja częściowa', ['pay R2 450.00', 'receipt R2', 'total'], [
        'PARTIAL AUTH-R2 brakuje 150.00',
        'Platnosc R2: 300.00 (AUTH-R2)',
        '300.00',
      ].join('\n'))
      .expect('granica 300.00 i grosz ponad', ['pay R3 300.00', 'pay R4 300.01', 'total'], [
        'PAID AUTH-R3',
        'PARTIAL AUTH-R4 brakuje 0.01',
        '600.00',
      ].join('\n'))
      .expect('odmowa i błąd kwoty', ['pay R5 1500.00', 'receipt R5', 'pay R6 0.00', 'receipt R6', 'total'], [
        'DECLINED 51',
        'Platnosc R5 nieudana: 51',
        'ERROR AMOUNT',
        'Platnosc R6 nieudana: AMOUNT',
        '0.00',
      ].join('\n'))
      .expect('brak płatności', ['receipt R9', 'total'], [
        'Brak platnosci R9',
        '0.00',
      ].join('\n'))
      .expect('ponowna płatność nadpisuje poprzednią', [
        'pay R1 45.00', 'pay R2 25.50', 'pay R1 1500.00', 'receipt R1', 'total'], [
        'PAID AUTH-R1',
        'PAID AUTH-R2',
        'DECLINED 51',
        'Platnosc R1 nieudana: 51',
        '25.50',
      ].join('\n'))
      .tests();
  });
});
