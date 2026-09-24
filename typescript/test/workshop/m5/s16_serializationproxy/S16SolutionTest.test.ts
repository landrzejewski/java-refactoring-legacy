import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { InvalidClassError, load, save } from '../../../../src/workshop/m5/s16_serializationproxy/SessionStore.js';
import { StudentTicket as Step1StudentTicket } from '../../../../src/workshop/m5/s16_serializationproxy/step1/StudentTicket.js';
import { StudentTicket as Step2StudentTicket } from '../../../../src/workshop/m5/s16_serializationproxy/step2/StudentTicket.js';
import { TicketPricing as Step2TicketPricing } from '../../../../src/workshop/m5/s16_serializationproxy/step2/TicketPricing.js';
import type { Pricing } from '../../../../src/workshop/m5/s16_serializationproxy/step3/Pricing.js';
import { TicketPricing as Step3TicketPricing } from '../../../../src/workshop/m5/s16_serializationproxy/step3/TicketPricing.js';
import { workshopDir } from '../../support/paths.js';
import { compileErrors } from '../reflection.js';

/**
 * "Plik z v1": StudentTicket('Amator', 'F3', 'S-123') zapisany przez klasę ze start (bez nadklasy).
 * Zamrożony jako stała, żeby zmiany start na żywo nie zmieniały danych historycznych.
 */
const SAVED_BY_V1 = '{"type":"StudentTicket","data":{"title":"Amator","seat":"F3","studentId":"S-123"}}';

/** Serializacja: dane zapisane przez start czytane przez nowsze kroki. Proxy: kiedy da się opakować serwis. */
describe('S16SolutionTest', () => {
  it('pulledUpFieldsAreSilentlyLostWhenReadingOldData', () => {
    const ticket = load(SAVED_BY_V1, Step1StudentTicket) as Step1StudentTicket;
    // pułapka: brak wersji formatu, brak wyjątku, utracone title i seat
    expect(ticket.describe()).toBe('undefined undefined (legitymacja S-123)');
  });

  it('serializationProxyRejectsOldDataLoudly', () => {
    expect(() => load(SAVED_BY_V1, Step2StudentTicket)).toThrow(InvalidClassError);
  });

  it('serializationProxyWritesFlatFormIndependentOfHierarchy', () => {
    const json = save(new Step2StudentTicket('Amator', 'F3', 'S-1'));
    const { data } = JSON.parse(json) as { data: object };
    expect(Object.keys(data)).toEqual(['v', 'title', 'seat', 'studentId']);
    // pola klasy bazowej (ani ich nazwy z kroku 1) nie są częścią formatu
    expect(json).not.toContain('_title');
  });

  // Java: jdkProxyCannotWrapFinalClassWithoutInterface. W TS klasa z polem # jest nominalna
  // (opakowania nie da się podstawić jako TicketPricing), a przezroczysty Proxy łamie dostęp do #.
  it('proxyCannotWrapClassWithPrivateFieldsWithoutInterface', () => {
    const probe = workshopDir('m5', 's16_serializationproxy', 'step2', '__probe__.ts');
    expect(compileErrors({
      [probe]: `import type { Money } from '../../../shared/Money.js';
        import { TicketPricing } from './TicketPricing.js';
        const target = new TicketPricing();
        export const audited: TicketPricing = { studentPrice: (basePrice: Money) => target.studentPrice(basePrice) };`,
    })).toEqual(['TS2741']);
    const transparent = new Proxy(new Step2TicketPricing(), {});
    expect(() => transparent.studentPrice(Money.of('32.00'))).toThrow(TypeError);
  });

  it('extractedInterfaceAllowsDynamicProxy', () => {
    let calls = 0;
    const target: Pricing = new Step3TicketPricing();
    // Proxy roli: każda metoda jest wołana na prawdziwym obiekcie (this = target), nie na proxy.
    const pricing: Pricing = new Proxy(target, {
      get: (object, key) => {
        const member: unknown = Reflect.get(object, key);
        return typeof member === 'function'
          ? (...args: unknown[]) => {
              calls++;
              return Reflect.apply(member, object, args);
            }
          : member;
      },
    });
    expect(pricing.studentPrice(Money.of('32.00'))).toEqual(Money.of('24.00'));
    expect(calls).toBe(1);
  });
});
