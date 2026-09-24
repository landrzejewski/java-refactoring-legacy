import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { StandardTicket } from '../../../../src/workshop/m5/s12_bridgemethods/StandardTicket.js';
import { StudentTicket } from '../../../../src/workshop/m5/s12_bridgemethods/StudentTicket.js';
import type { Ticket } from '../../../../src/workshop/m5/s12_bridgemethods/Ticket.js';
import type { PriceRule } from '../../../../src/workshop/m5/s12_bridgemethods/step1/PriceRule.js';
import { RuleRegistry as Step1RuleRegistry } from '../../../../src/workshop/m5/s12_bridgemethods/step1/RuleRegistry.js';
import { StudentRule as Step1StudentRule } from '../../../../src/workshop/m5/s12_bridgemethods/step1/StudentRule.js';
import { RuleRegistry as Step2RuleRegistry } from '../../../../src/workshop/m5/s12_bridgemethods/step2/RuleRegistry.js';
import { StandardRule as Step2StandardRule } from '../../../../src/workshop/m5/s12_bridgemethods/step2/StandardRule.js';

/**
 * W Javie: metody bridge - niewidoczne w źródle, widoczne dla refleksji i surowych wywołań.
 * W TS generyki znikają całkowicie: nie ma mostów, ale nie ma też typu T w czasie działania,
 * a biwariancja metod pozwala wywołać regułę z niewłaściwym biletem bez żadnego rzutowania.
 */
describe('S12SolutionTest', () => {
  // Java: genericInterfaceAddsSyntheticBridgeMethod (dwie metody apply, jedna to bridge).
  it('genericInterfaceAddsNoRuntimeMethod', () => {
    const apply = Object.getOwnPropertyNames(Step1StudentRule.prototype).filter((name) => name === 'apply');
    expect(apply).toHaveLength(1);
    expect(Step1StudentRule.prototype.apply).toHaveLength(1);
  });

  // Java: naiveReflectionWouldRegisterErasedType. W TS "refleksja" widzi tylko nazwę klasy -
  // reguła o innej nazwie rejestruje się pod zmyślonym typem, a jej T jest nieosiągalne.
  it('naiveReflectionWouldRegisterGuessedType', () => {
    class DiscountRule implements PriceRule<StudentTicket> {
      apply(ticket: StudentTicket): Money {
        return ticket.basePrice().percent(50);
      }
    }
    expect(new Step1RuleRegistry(new DiscountRule()).supportedTypes()).toEqual(['DiscountTicket']);
  });

  // Java: rawCallThroughBridgeFailsWithClassCastException. W TS brak mostu z rzutowaniem:
  // przypisanie do PriceRule<Ticket> przechodzi (biwariancja), a zły bilet jest wyceniany po cichu.
  it('erasedCallWithWrongTicketSilentlyMisprices', () => {
    const erased: PriceRule<Ticket> = new Step1StudentRule();
    expect(erased.apply(new StandardTicket(Money.of('25.00')))).toEqual(Money.of('18.75'));
  });

  it('solutionReportsMissingRuleExplicitly', () => {
    const registry = new Step2RuleRegistry(new Step2StandardRule());
    expect(() => registry.price(new StudentTicket(Money.of('25.00'), 'S-1'))).toThrow(IllegalStateError);
  });
});
