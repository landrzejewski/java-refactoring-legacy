import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { PriceRequest } from '../../../../src/workshop/m6/s01_strategy/PriceRequest.js';
import * as start from '../../../../src/workshop/m6/s01_strategy/start/PriceBoard.js';
import * as step1 from '../../../../src/workshop/m6/s01_strategy/step1/PriceBoard.js';
import * as step2 from '../../../../src/workshop/m6/s01_strategy/step2/PriceBoard.js';
import * as step3 from '../../../../src/workshop/m6/s01_strategy/step3/PriceBoard.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

function request(base: string, type: string, program: string | null): PriceRequest {
  return new PriceRequest(Money.of(base), type, program);
}

function run(price: () => Money): string {
  try {
    return price().toString();
  } catch (error) {
    if (error instanceof IllegalArgumentError) {
      return `ERROR: ${error.message}`;
    }
    throw error;
  }
}

/** Tabela decyzji programu zniżek: każda gałąź, typ nieznany, program nieznany i null. */
describe('S01EquivalenceTest', () => {
  describe('everyStepPricesTheSame', () => {
    Scene.variants<PriceRequest, string>()
      .variant('start', (r) => run(() => new start.PriceBoard().priceFor(r)))
      .variant('step1', (r) => run(() => new step1.PriceBoard().priceFor(r)))
      .variant('step2', (r) => run(() => new step2.PriceBoard().priceFor(r)))
      .variant('step3', (r) => run(() => new step3.PriceBoard().priceFor(r)))
      .expect('STANDARD normalny 2D', request('25.00', 'N', 'STANDARD'), '25.00')
      .expect('STANDARD student 3D', request('32.00', 'S', 'STANDARD'), '24.00')
      .expect('STANDARD senior IMAX', request('40.00', 'E', 'STANDARD'), '28.00')
      .expect('STANDARD dziecko 2D', request('25.00', 'C', 'STANDARD'), '15.00')
      .expect('STUDENT_WEEK student IMAX', request('40.00', 'S', 'STUDENT_WEEK'), '20.00')
      .expect('STUDENT_WEEK dziecko 3D', request('32.00', 'C', 'STUDENT_WEEK'), '19.20')
      .expect('PREMIERE dziecko IMAX', request('40.00', 'C', 'PREMIERE'), '40.00')
      .expect('PREMIERE nie sprawdza typu', request('40.00', 'X', 'PREMIERE'), '40.00')
      .expect('STANDARD nieznany typ', request('25.00', 'X', 'STANDARD'), 'ERROR: unknown ticket type: X')
      .expect('nieznany program', request('25.00', 'N', 'BLACK_FRIDAY'), 'ERROR: unknown program: BLACK_FRIDAY')
      .expect('program null', request('25.00', 'N', null), 'ERROR: program must not be null')
      .tests();
  });
});
