import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { RefundDecision } from '../../../../src/workshop/m5/s28_chain/RefundDecision.js';
import { RefundRequest } from '../../../../src/workshop/m5/s28_chain/RefundRequest.js';
import { AfterScreeningHandler } from '../../../../src/workshop/m5/s28_chain/step3/AfterScreeningHandler.js';
import { AutoRefundHandler } from '../../../../src/workshop/m5/s28_chain/step3/AutoRefundHandler.js';
import { CashierHandler } from '../../../../src/workshop/m5/s28_chain/step3/CashierHandler.js';
import { ManagerHandler } from '../../../../src/workshop/m5/s28_chain/step3/ManagerHandler.js';
import { NoApproverHandler } from '../../../../src/workshop/m5/s28_chain/step3/NoApproverHandler.js';
import { RefundApprover } from '../../../../src/workshop/m5/s28_chain/step3/RefundApprover.js';
import type { RefundHandler } from '../../../../src/workshop/m5/s28_chain/step3/RefundHandler.js';

const START = LocalDateTime.of(2026, 10, 20, 18, 0);

function request(amount: string, requestedAt: LocalDateTime): RefundRequest {
  return new RefundRequest(Money.of(amount), START, requestedAt);
}

/** Łańcuch: jawnie składany, ogniwo zatrzymuje albo przekazuje - w odróżnieniu od dekoratora. */
describe('S28SolutionTest', () => {
  it('chainIsAssembledExplicitlySoAFestivalCanSkipAutoRefunds', () => {
    const festival = new RefundApprover(
      new AfterScreeningHandler(new CashierHandler(Money.of('100.00'), new NoApproverHandler())));
    expect(festival.decide(request('50.00', START.plusDays(-3))).toString()).toBe('APPROVED CASHIER');
    expect(festival.decide(request('500.00', START.plusDays(-3))).toString())
      .toBe('REJECTED brak uprawnionego zatwierdzajacego');
  });

  it('handlerThatDecidesStopsTheChainUnlikeADecorator', () => {
    let calls = 0;
    const spy: RefundHandler = {
      handle: () => {
        calls++;
        return RefundDecision.rejected('spy');
      },
    };
    const cashier = new CashierHandler(Money.of('100.00'), spy);

    expect(cashier.handle(request('40.00', START.minusHours(2))).toString()).toBe('APPROVED CASHIER');
    expect(calls, 'obsłużone żądanie nie idzie dalej').toBe(0);

    expect(cashier.handle(request('400.00', START.minusHours(2))).toString()).toBe('REJECTED spy');
    expect(calls, 'nieobsłużone żądanie idzie dokładnie do następnika').toBe(1);
  });

  // Bez refleksji typów pól: ogniwo dostaje rozpoznawalnego następnika i liczymy pola obiektu,
  // które go wskazują (dokładnie jedno); koniec łańcucha nie ma żadnych pól.
  it('everyLinkKnowsOnlyItsSuccessor', () => {
    const successor: RefundHandler = new NoApproverHandler();
    const links: Record<string, object> = {
      AfterScreeningHandler: new AfterScreeningHandler(successor),
      AutoRefundHandler: new AutoRefundHandler(successor),
      CashierHandler: new CashierHandler(Money.of('100.00'), successor),
      ManagerHandler: new ManagerHandler(Money.of('1000.00'), successor),
    };
    for (const [name, link] of Object.entries(links)) {
      const successors = Object.values(link).filter((field) => field === successor).length;
      expect(successors, name).toBe(1);
      const handlerFields = Object.values(link).filter((field) => typeof Reflect.get(Object(field), 'handle') === 'function');
      expect(handlerFields.length, name).toBe(1);
    }
    expect(Object.keys(new NoApproverHandler()).length, 'koniec łańcucha nie ma następnika').toBe(0);
  });
});
