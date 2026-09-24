/**
 * Krok 2: format kwot ("114.00") potrzebny i CinemaManager, i NotificationService - jeden właściciel.
 * Eksportowany tylko na potrzeby klas kroku (w Javie klasa pakietowa).
 */
export class Formats {
  private constructor() {}

  static amount(value: number): string {
    return value.toFixed(2);
  }
}
