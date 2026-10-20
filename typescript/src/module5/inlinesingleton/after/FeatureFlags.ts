import { isBlank, unmodifiableSet } from '../../support.js';

/**
 * Inline Singleton: zwykły, niezmienny obiekt wartości zamiast globalnej instancji.
 * Tworzony w korzeniu kompozycji i przekazywany przez konstruktor, więc zależność
 * jest jawna, a każdy test może mieć własny zestaw flag bez sprzątania globalnego stanu.
 */
export class FeatureFlags {
  readonly enabled: ReadonlySet<string>;

  constructor(enabled: Iterable<string>) {
    // Odpowiednik Set.copyOf(enabled) w kompaktowym konstruktorze rekordu.
    this.enabled = unmodifiableSet(enabled);
  }

  static of(...flags: string[]): FeatureFlags {
    return new FeatureFlags(flags);
  }

  /**
   * Konfiguracja w postaci "canary,dark-launch"; pusty tekst oznacza brak flag.
   */
  static parse(config: string | null | undefined): FeatureFlags {
    if (config === null || config === undefined || isBlank(config)) {
      return FeatureFlags.of();
    }
    return new FeatureFlags(
      config.split(',')
        .map(flag => flag.trim())
        .filter(flag => flag !== ''),
    );
  }

  isEnabled(flag: string): boolean {
    return this.enabled.has(flag);
  }

  // Odpowiednik equals() rekordu Javy (Set.equals: te same elementy).
  equals(other: unknown): boolean {
    return other instanceof FeatureFlags
      && this.enabled.size === other.enabled.size
      && [...this.enabled].every(flag => other.enabled.has(flag));
  }
}
