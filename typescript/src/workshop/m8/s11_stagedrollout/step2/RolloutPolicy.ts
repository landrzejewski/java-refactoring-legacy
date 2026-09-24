import { crc32 } from 'node:zlib';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 2: deterministyczny podział - boolean zastąpiony procentem klientów. Klient trafia do
 * koszyka 0-99 ze stabilnego skrótu (CRC32) znormalizowanego e-maila: ten sam klient zawsze
 * dostaje tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca z nowej ścieżki.
 */
export class RolloutPolicy {
  readonly allowList: ReadonlySet<string>;

  constructor(readonly percent: number, allowList: Iterable<string>) {
    if (percent < 0 || percent > 100) {
      throw new IllegalArgumentError('Procent spoza 0-100: ' + percent);
    }
    this.allowList = new Set(allowList);
  }

  /** Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy. */
  static current(): RolloutPolicy {
    return new RolloutPolicy(0, ['anna@kino.pl', 'jan@kino.pl']);
  }

  allows(email: string): boolean {
    return this.allowList.has(RolloutPolicy.normalize(email)) || RolloutPolicy.bucket(email) < this.percent;
  }

  /** Koszyk 0-99 - stabilny między restartami i maszynami (nie losowanie, nie adres obiektu). */
  static bucket(email: string): number {
    return crc32(RolloutPolicy.normalize(email)) % 100;
  }

  private static normalize(email: string): string {
    return email.trim().toLowerCase();
  }
}
