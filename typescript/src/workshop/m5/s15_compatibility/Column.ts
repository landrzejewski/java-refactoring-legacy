/**
 * Stabilny kontrakt sceny: metadane kolumny eksportu przypięte do metody - odpowiednik adnotacji
 * @Column("cena") czytanej refleksją (jak w ORM/JSON/CSV). Dekoratory TC39 nie działają jeszcze
 * w całym łańcuchu narzędzi (Node 22, vitest), więc klasa oznacza metodę w bloku statycznym:
 *
 *   static {
 *     column(this.prototype.price, 'cena');
 *   }
 *
 * Metadane są przypięte do obiektu funkcji, a więc do klasy, która metodę DEKLARUJE.
 */
const columns = new WeakMap<object, string>();

export function column(method: object, name: string): void {
  columns.set(method, name);
}

/** Nazwa kolumny metody albo undefined (odpowiednik method.getAnnotation(Column.class)). */
export function columnOf(method: unknown): string | undefined {
  return typeof method === 'function' ? columns.get(method) : undefined;
}
