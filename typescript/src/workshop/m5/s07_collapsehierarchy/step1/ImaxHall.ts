import { Hall } from './Hall.js';

/** Krok 1: usunięte override'y, które tylko wołały super (metoda identyczna z metodą nadklasy). */
export class ImaxHall extends Hall {
  constructor(name: string, rows: number, seatsPerRow: number) {
    super(name, rows, seatsPerRow, rows - 1);
  }
}
