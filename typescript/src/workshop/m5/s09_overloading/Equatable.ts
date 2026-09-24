/**
 * Stabilny kontrakt sceny: równość wartości - odpowiednik Object.equals(Object) w Javie
 * (JS go nie ma: Array.includes, Set i Map porównują tożsamość, equals nie widzą).
 *
 * Sygnatura zapisana jako właściwość z typem funkcyjnym, a NIE jako metoda. Parametry metod
 * TypeScript sprawdza biwariantnie (dziura w typowaniu), więc implementacja `equals(other: Ticket)`
 * przeszłaby przez kontrakt `equals(other: unknown): boolean` bez słowa - jak przeciążenie
 * equals(Ticket) w Javie. Typ funkcyjny jest sprawdzany kontrawariantnie (strictFunctionTypes):
 * zawężony parametr to błąd kompilacji TS2416 - tak jak @Override zamienia cichy błąd w błąd kompilacji.
 */
export interface Equatable {
  readonly equals: (other: unknown) => boolean;
}

/** Odpowiednik List.contains(Object): kolekcja pyta o równość przez kontrakt, a nie przez ===. */
export function containsEqual(items: readonly Equatable[], candidate: unknown): boolean {
  return items.some((item) => item.equals(candidate));
}
