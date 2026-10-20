import { readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Outcome, PaymentConfirmation } from '../../../../src/workshop/m5/s29_boundaryobject/step3/PaymentConfirmation.js';
import { PaymentService } from '../../../../src/workshop/m5/s29_boundaryobject/step3/PaymentService.js';
import { TerminalResponseMapper } from '../../../../src/workshop/m5/s29_boundaryobject/step3/TerminalResponseMapper.js';
import { workshopDir } from '../../support/paths.js';

const SERVICE = workshopDir('m5', 's29_boundaryobject', 'step3', 'PaymentService.ts');

/** Obiekt graniczny jest zawsze poprawny, format terminala zna tylko granica, domena widzi port. */
describe('S29SolutionTest', () => {
  const mapper = new TerminalResponseMapper();

  it('boundaryObjectCannotBeInvalid', () => {
    expect(() => new PaymentConfirmation(Outcome.APPROVED, ' ', Money.of('10.00'))).toThrow(IllegalArgumentError);
    expect(() => new PaymentConfirmation(Outcome.APPROVED, 'AUTH', Money.of('-0.01'))).toThrow(IllegalArgumentError);
    expect(() => new PaymentConfirmation(Outcome.DECLINED, '51', Money.of('10.00'))).toThrow(IllegalArgumentError);
  });

  it('malformedResponseIsRejectedAtTheBoundary', () => {
    for (const raw of ['OK;AUTH-R1', 'MAYBE;X;0', 'OK;;4500', 'OK;AUTH-R1;abc']) {
      expect(() => mapper.toConfirmation(raw), raw).toThrow(IllegalArgumentError);
    }
  });

  it('mapperTranslatesUnitsBothWays', () => {
    expect(mapper.toGrosze(Money.of('45.00'))).toBe(4500);
    expect(mapper.toConfirmation('OK;AUTH-R1;4500'))
      .toEqual(new PaymentConfirmation(Outcome.APPROVED, 'AUTH-R1', Money.of('45.00')));
  });

  it('serviceWorksWithAnyTerminalBehindThePort', () => {
    const service = new PaymentService({
      authorize: (bookingId, amount) => new PaymentConfirmation(Outcome.APPROVED, `FAKE-${bookingId}`, amount),
    });
    expect(service.pay('R1', Money.of('999.00'))).toBe('PAID FAKE-R1');
    expect(service.totalAuthorized()).toEqual(Money.of('999.00'));
  });

  // TypeScript nie ma refleksji typów pól i parametrów: sprawdzamy kod serwisu
  // (bez komentarzy), że nie importuje ani nie wymienia klienta terminala i mappera.
  it('domainServiceSeesNeitherTheLegacyClientNorTheMapper', () => {
    const code = readFileSync(SERVICE, 'utf8').replace(/\/\*[\s\S]*?\*\/|\/\/.*$/gm, '');
    expect(code, 'serwis nie zna klienta terminala').not.toMatch(/\bLegacyTerminalClient\b/);
    expect(code, 'serwis nie zna formatu odpowiedzi').not.toMatch(/\bTerminalResponseMapper\b/);
  });
});
