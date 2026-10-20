import { readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { Basket } from '../../../../src/workshop/m5/s32_mediator/step3/Basket.js';
import { PayButton } from '../../../../src/workshop/m5/s32_mediator/step3/PayButton.js';
import { PriceSummary } from '../../../../src/workshop/m5/s32_mediator/step3/PriceSummary.js';
import { SeatMap } from '../../../../src/workshop/m5/s32_mediator/step3/SeatMap.js';
import type { SelectionMediator } from '../../../../src/workshop/m5/s32_mediator/step3/SelectionMediator.js';
import { workshopDir } from '../../support/paths.js';

const COMPONENTS = [SeatMap, Basket, PriceSummary, PayButton] as const;

class RecordingMediator implements SelectionMediator {
  readonly events: string[] = [];

  seatToggled(seat: string, selected: boolean): void {
    this.events.push(`seatToggled ${seat} ${selected}`);
  }

  seatRemoved(seat: string): void {
    this.events.push(`seatRemoved ${seat}`);
  }

  basketCleared(): void {
    this.events.push('basketCleared');
  }

  voucherApplied(): void {
    this.events.push('voucherApplied');
  }

  payRequested(): string {
    this.events.push('payRequested');
    return 'ok';
  }
}

/** Krok 3: komponenty znają tylko mediatora i dają się testować z atrapą mediatora. */
describe('S32SolutionTest', () => {
  // TypeScript nie ma refleksji typów pól. Zamiast typów pól sprawdzamy:
  // - importy modułu: jedyny typ sceny, który zna komponent, to SelectionMediator,
  // - pola instancji: jedynym współpracownikiem jest przekazany mediator.
  it('everyComponentKnowsOnlyTheMediator', () => {
    for (const component of COMPONENTS) {
      const source = readFileSync(workshopDir('m5', 's32_mediator', 'step3', `${component.name}.ts`), 'utf8');
      const sceneImports = [...source.matchAll(/from '\.\/(\w+)\.js'/g)].map((match) => match[1]);
      expect(sceneImports, component.name).toEqual(['SelectionMediator']);

      const mediator = new RecordingMediator();
      const collaborators = Object.values(new component(mediator))
        .filter((value) => value === mediator || COMPONENTS.some((type) => value instanceof type));
      expect(collaborators, component.name).toEqual([mediator]);
    }
  });

  it('componentReportsWhatHappenedWithoutKnowingWhoReacts', () => {
    const mediator = new RecordingMediator();
    const map = new SeatMap(mediator);
    map.click('A1');
    map.click('B2');
    map.click('A1');
    new PriceSummary(mediator).applyVoucher('KINO10');
    new PayButton(mediator).press();
    expect(mediator.events, 'sprzedane B2 nie generuje zdarzenia; nieaktywny przycisk nie prosi o płatność')
      .toEqual(['seatToggled A1 true', 'seatToggled A1 false', 'voucherApplied']);
  });
});
