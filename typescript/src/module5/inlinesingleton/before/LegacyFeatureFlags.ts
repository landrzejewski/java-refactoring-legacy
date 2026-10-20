/**
 * Globalny, mutowalny singleton. Każdy może go zmienić z dowolnego miejsca, a klasy,
 * które z niego korzystają, nie pokazują tej zależności w konstruktorze.
 * (synchronized z Javy pominięte - JavaScript jest jednowątkowy.)
 */
export class LegacyFeatureFlags {
  private static readonly INSTANCE = new LegacyFeatureFlags();

  private readonly enabled = new Set<string>();

  private constructor() {}

  static getInstance(): LegacyFeatureFlags {
    return LegacyFeatureFlags.INSTANCE;
  }

  enable(flag: string): void {
    this.enabled.add(flag);
  }

  disable(flag: string): void {
    this.enabled.delete(flag);
  }

  isEnabled(flag: string): boolean {
    return this.enabled.has(flag);
  }

  // istnieje tylko po to, żeby testy mogły posprzątać globalny stan
  reset(): void {
    this.enabled.clear();
  }
}
