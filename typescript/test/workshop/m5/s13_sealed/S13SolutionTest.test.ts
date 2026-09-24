import { readFileSync } from 'node:fs';

import ts from 'typescript-api';
import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { PriceCalculator as StartPriceCalculator } from '../../../../src/workshop/m5/s13_sealed/start/PriceCalculator.js';
import type { Ticket as StartTicket } from '../../../../src/workshop/m5/s13_sealed/start/Ticket.js';
import { PriceCalculator as Step2PriceCalculator } from '../../../../src/workshop/m5/s13_sealed/step2/PriceCalculator.js';
import type { Ticket as Step2Ticket } from '../../../../src/workshop/m5/s13_sealed/step2/Ticket.js';
import { ChildTicket } from '../../../../src/workshop/m5/s13_sealed/step3/ChildTicket.js';
import { PriceCalculator as Step3PriceCalculator } from '../../../../src/workshop/m5/s13_sealed/step3/PriceCalculator.js';
import { workshopDir } from '../../support/paths.js';
import { compileErrors, sourceOf } from '../reflection.js';

/** Otwarta hierarchia kontra zamknięta unia: cichy błąd, błąd kompilacji i błąd w starym kodzie JS. */
describe('S13SolutionTest', () => {
  /*
   * Pułapka otwartej hierarchii: typ dopisany "z zewnątrz" - tu zwykły literał, bo przy typowaniu
   * strukturalnym każdy obiekt o kształcie Ticket jest biletem - dostaje po cichu 0% zniżki.
   * Po kroku 1 wykonanym na start ten plik przestanie się typować (tsc), a test zrobi się czerwony.
   */
  it('startSilentlyGivesUnknownTicketNoDiscount', () => {
    const child: StartTicket = { kind: 'CHILD', basePrice: () => Money.of('25.00') };
    // pułapka: bilet dziecięcy (40%) policzony jak normalny
    expect(new StartPriceCalculator().price(child)).toEqual(Money.of('25.00'));
  });

  it('sealedHierarchyListsAllVariants', () => {
    // Odpowiednik isSealed() + getPermittedSubclasses(): Ticket to unia dokładnie tych wariantów.
    const source = sourceOf('s13_sealed', 'step1', 'Ticket.ts');
    const alias = source.statements.find(ts.isTypeAliasDeclaration);
    expect(alias !== undefined && ts.isUnionTypeNode(alias.type)).toBe(true);
    expect(alias?.type.getText(source).split('|').map((part) => part.trim()))
      .toEqual(['StandardTicket', 'StudentTicket', 'SeniorTicket']);
    // ...i kompilator nie wpuszcza obcego obiektu o tym samym kształcie.
    const probe = workshopDir('m5', 's13_sealed', 'step1', '__probe__.ts');
    expect(compileErrors({
      [probe]: `import { Money } from '../../../shared/Money.js';
        import type { Ticket } from './Ticket.js';
        export const child: Ticket = { kind: 'CHILD', basePrice: () => Money.of('25.00') };`,
    })).toEqual(['TS2322']);
  });

  it('solutionHandlesChildTicket', () => {
    expect(new Step3PriceCalculator().price(new ChildTicket(Money.of('25.00')))).toEqual(Money.of('15.00'));
  });

  /*
   * Odpowiednik MatchException: wyczerpanie sprawdza tylko kompilator. Kod JS zbudowany przeciw
   * step2 (3 warianty) - np. osobno opublikowany pakiet - dostaje w czasie działania wariant ze step3
   * i kończy na assertNever.
   */
  it('oldExhaustiveSwitchThrowsMatchExceptionForNewVariant', () => {
    const child = new ChildTicket(Money.of('25.00')) as unknown as Step2Ticket;
    expect(() => new Step2PriceCalculator().discountPercent(child)).toThrow('Unexpected value');
  });

  // Dodatkowy test TS: nowy wariant w unii wywraca kompilację switcha bez swojego case
  // (TS2345: 'ChildTicket' nie jest 'never'), a gałąź default z wynikiem wyłącza to sprawdzenie.
  it('newVariantBreaksCompilationOfExhaustiveSwitch', () => {
    const probe = workshopDir('m5', 's13_sealed', 'step3', '__probe__.ts');
    const oldCalculator = readFileSync(workshopDir('m5', 's13_sealed', 'step2', 'PriceCalculator.ts'), 'utf8');
    expect(compileErrors({ [probe]: oldCalculator })).toEqual(['TS2345']);
    const withDefault = oldCalculator.replace('default: return assertNever(ticket);', 'default: return 0;');
    expect(compileErrors({ [probe]: withDefault })).toEqual([]);
  });
});
