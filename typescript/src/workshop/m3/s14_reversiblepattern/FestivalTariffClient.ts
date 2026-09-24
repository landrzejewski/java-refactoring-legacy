/**
 * Obcy interfejs (stabilny, nie nasz): klient systemu festiwalu filmowego.
 * Zwraca tygodniową opłatę w groszach jako liczbę całkowitą - inny model niż nasz Decimal.
 */
export class FestivalTariffClient {
  weeklyFeeInCents(_title: string, week: number): number {
    return week <= 2 ? 30_000 : 15_000;
  }
}
