import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m6/s10_implicittree/start/BarMenu.js';
import { MenuMapper } from '../../../../src/workshop/m6/s10_implicittree/step1/MenuMapper.js';
import * as step3 from '../../../../src/workshop/m6/s10_implicittree/step3/BarMenu.js';
import { safe } from './safe.js';

/**
 * Odpowiednik java.util.Random (ten sam generator liniowy kongruencyjny), żeby test losował
 * dokładnie te same drzewa co wersja Java dla ziarna 42.
 */
class JavaRandom {
  private static readonly MULTIPLIER = 0x5DEECE66Dn;
  private static readonly MASK = (1n << 48n) - 1n;
  private seed: bigint;

  constructor(seed: number) {
    this.seed = (BigInt(seed) ^ JavaRandom.MULTIPLIER) & JavaRandom.MASK;
  }

  nextInt(bound: number): number {
    if ((bound & -bound) === bound) {
      return Number((BigInt(bound) * BigInt(this.next(31))) >> 31n);
    }
    let bits: number;
    let value: number;
    do {
      bits = this.next(31);
      value = bits % bound;
    } while (((bits - value + (bound - 1)) | 0) < 0);
    return value;
  }

  private next(bits: number): number {
    this.seed = (this.seed * JavaRandom.MULTIPLIER + 0xBn) & JavaRandom.MASK;
    return Number(BigInt.asIntN(32, this.seed >> BigInt(48 - bits)));
  }
}

function randomCombo(random: JavaRandom, depth: number): unknown[] {
  const combo: unknown[] = [];
  combo.push(`Zestaw ${random.nextInt(100)}`);
  const size = random.nextInt(4);
  for (let i = 0; i < size; i++) {
    const kind = random.nextInt(20);
    if (kind === 0) {
      combo.push(7);
    } else if (kind === 1) {
      combo.push('Bez ceny');
    } else if (kind < 6 && depth < 3) {
      combo.push(randomCombo(random, depth + 1));
    } else {
      combo.push(`P${random.nextInt(50)}=${random.nextInt(30)}.${random.nextInt(10)}0`);
    }
  }
  return combo;
}

/**
 * Test różnicowy: stara reprezentacja (start) kontra nowa na 500 losowych drzewach
 * (stałe ziarno - wynik powtarzalny). Uzupełnia, a nie zastępuje niezależne oczekiwania.
 */
describe('S10DifferentialTest', () => {
  const legacyMenu = new start.BarMenu();
  const legacy = safe((d) => legacyMenu.price(d), (d) => legacyMenu.render(d));

  function compare(candidate: (definition: readonly unknown[]) => string): void {
    const random = new JavaRandom(42);
    for (let i = 0; i < 500; i++) {
      const definition = randomCombo(random, 0);
      expect(candidate(definition), `drzewo: ${JSON.stringify(definition)}`).toBe(legacy(definition));
    }
  }

  it('compositeFromMapperMatchesLegacyAlready', () => {
    compare(safe(
      (definition) => MenuMapper.fromNested(definition).price(),
      (definition) => {
        const text: string[] = [];
        MenuMapper.fromNested(definition).render(0, text);
        return text.join('');
      }));
  });

  it('finalBarMenuMatchesLegacy', () => {
    const menu = new step3.BarMenu();
    compare(safe((d) => menu.price(d), (d) => menu.render(d)));
  });
});
