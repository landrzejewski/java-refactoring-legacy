import { Hall } from './Hall.js';

/**
 * Start: podklasa bez własnego stanu. Override'y tylko wołają super, a jedyna różnica to reguła
 * w konstruktorze (VIP w dwóch ostatnich rzędach). Nikt nie sprawdza instanceof ImaxHall.
 */
export class ImaxHall extends Hall {
  constructor(name: string, rows: number, seatsPerRow: number) {
    super(name, rows, seatsPerRow, rows - 1);
  }

  override capacity(): number {
    return super.capacity();
  }

  override describe(): string {
    return super.describe();
  }
}
