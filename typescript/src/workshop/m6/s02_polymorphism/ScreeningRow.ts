/**
 * Stabilny kontrakt sceny: wiersz z bazy repertuaru. Znaczenie value zależy od rodzaju:
 * REGULAR i PREMIERE - długość filmu w minutach, MARATHON - liczba filmów.
 */
export class ScreeningRow {
  constructor(
    readonly kind: string,
    readonly title: string,
    readonly value: number,
  ) {}
}
