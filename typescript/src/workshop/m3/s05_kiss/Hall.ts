/**
 * Stabilny kontrakt sceny - plan sali. Rzędy numerowane od 1.
 * Znaki w rzędzie: '.' wolne, 'X' zajęte, 'B' zablokowane (awaria), ' ' przejście.
 *
 * @param vipFromRow od tego rzędu (włącznie) miejsca są VIP
 */
export class Hall {
  readonly rows: readonly string[];

  constructor(rows: readonly string[], readonly vipFromRow: number) {
    this.rows = Object.freeze([...rows]);
  }
}
