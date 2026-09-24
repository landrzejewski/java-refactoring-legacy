import { crc32 } from 'node:zlib';

import { IllegalArgumentError } from '../../../../shared/errors.js';

/**
 * Krok 3: wyłącznik awaryjny (kill switch). Ma pierwszeństwo przed wszystkim - także przed
 * listą wyjątków - bo służy do natychmiastowego wycofania bez wydania nowej wersji.
 */
export class RolloutPolicy {
  readonly allowList: ReadonlySet<string>;

  constructor(readonly percent: number, allowList: Iterable<string>, readonly killSwitch: boolean) {
    if (percent < 0 || percent > 100) {
      throw new IllegalArgumentError('Procent spoza 0-100: ' + percent);
    }
    this.allowList = new Set(allowList);
  }

  /** Dotychczasowe ustawienia produkcyjne: 0% ruchu + testerzy, wyłącznik nieaktywny. */
  static current(): RolloutPolicy {
    return new RolloutPolicy(0, ['anna@kino.pl', 'jan@kino.pl'], false);
  }

  allows(email: string): boolean {
    if (this.killSwitch) {
      return false;
    }
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
