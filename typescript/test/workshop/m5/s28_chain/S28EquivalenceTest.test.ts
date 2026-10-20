import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import type { RefundDecision } from '../../../../src/workshop/m5/s28_chain/RefundDecision.js';
import { RefundRequest } from '../../../../src/workshop/m5/s28_chain/RefundRequest.js';
import * as start from '../../../../src/workshop/m5/s28_chain/start/RefundApprover.js';
import * as step1 from '../../../../src/workshop/m5/s28_chain/step1/RefundApprover.js';
import * as step2 from '../../../../src/workshop/m5/s28_chain/step2/RefundApprover.js';
import * as step3 from '../../../../src/workshop/m5/s28_chain/step3/RefundApprover.js';
import { Scene } from '../../support/scene.js';

const START = LocalDateTime.of(2026, 10, 20, 18, 0);

function request(amount: string, requestedAt: LocalDateTime): RefundRequest {
  return new RefundRequest(Money.of(amount), START, requestedAt);
}

function safe(approver: { decide(request: RefundRequest): RefundDecision }): (request: RefundRequest) => string {
  return (request) => {
    try {
      return approver.decide(request).toString();
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}

/**
 * Tabela decyzji zwrotu: każdy poziom, granice limitów i 24 h, kolejność reguł (AUTO bez limitu,
 * odrzucenie po seansie przed wszystkim) oraz walidacja kwoty przed łańcuchem.
 */
describe('S28EquivalenceTest', () => {
  describe('everyStepDecidesTheSame', () => {
    Scene.variants<RefundRequest, string>()
      .variant('start', safe(new start.RefundApprover()))
      .variant('step1', safe(new step1.RefundApprover()))
      .variant('step2', safe(new step2.RefundApprover()))
      .variant('step3', safe(new step3.RefundApprover()))
      .expect('48 h przed: automatycznie', request('50.00', START.minusHours(48)), 'APPROVED AUTO')
      .expect('dokładnie 24 h przed: automatycznie', request('50.00', START.minusHours(24)), 'APPROVED AUTO')
      .expect('23 h 59 min przed: kasjer', request('50.00', START.minusHours(24).plusMinutes(1)), 'APPROVED CASHIER')
      .expect('limit kasjera włącznie', request('100.00', START.minusHours(2)), 'APPROVED CASHIER')
      .expect('grosz ponad limit kasjera: kierownik', request('100.01', START.minusHours(2)), 'APPROVED MANAGER')
      .expect('limit kierownika włącznie', request('1000.00', START.minusHours(2)), 'APPROVED MANAGER')
      .expect('ponad limit kierownika: koniec łańcucha', request('1000.01', START.minusHours(2)),
        'REJECTED brak uprawnionego zatwierdzajacego')
      .expect('AUTO nie ma limitu kwoty (kolejność reguł)', request('5000.00', START.plusDays(-3)), 'APPROVED AUTO')
      .expect('w chwili rozpoczęcia seansu', request('10.00', START), 'REJECTED seans juz sie rozpoczal')
      .expect('po seansie, nawet mała kwota', request('10.00', START.plusHours(1)), 'REJECTED seans juz sie rozpoczal')
      .expect('kwota zero', request('0.00', START.minusHours(2)), 'ERROR amount must be positive')
      .expect('walidacja przed łańcuchem', request('-5.00', START.plusHours(1)), 'ERROR amount must be positive')
      .tests();
  });
});
