/** Wiersz: stara kolumna csv i nowa kolumna payload (każda może być null). */
export class Row {
  constructor(readonly csv: string | null, readonly payload: string | null) {}

  static withPayload(payload: string): Row {
    return new Row(null, payload);
  }
}

/**
 * Tabela rezerwacji po migracji schematu "expand": obok starej kolumny csv jest nowa,
 * dopuszczająca null kolumna payload. Samo dodanie kolumny nic nie psuje - stary kod
 * jej nie zna. Usunięcie kolumny csv to osobna migracja po zamknięciu okna wycofania.
 */
export class BookingTable {
  private readonly rows = new Map<string, Row>();

  put(id: string, row: Row): void {
    this.rows.set(id, row);
  }

  get(id: string): Row | undefined {
    return this.rows.get(id);
  }

  ids(): readonly string[] {
    return Object.freeze([...this.rows.keys()].sort());
  }
}
