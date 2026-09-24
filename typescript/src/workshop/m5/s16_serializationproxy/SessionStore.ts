import { IllegalStateError } from '../../../shared/errors.js';

/**
 * Stabilny kontrakt sceny: "platforma" serializacji (odpowiednik ObjectOutputStream/ObjectInputStream),
 * np. sesja HTTP, cache, kolejka.
 *
 * Zapis: JSON z nazwą klasy i danymi. JSON.stringify bierze WŁASNE pola obiektu pod ich nazwami
 * (pola prywatne ES - # - są pomijane) albo wynik toJSON(), jeśli klasa go ma (odpowiednik writeReplace).
 * Odczyt: jeśli klasa ma statyczne fromJSON (odpowiednik readResolve), to ono buduje obiekt. W przeciwnym
 * razie obiekt powstaje BEZ konstruktora, a pola dostają wartości po nazwach - jak domyślna deserializacja
 * Javy. Przywracane są tylko dane: klucze, które są już nazwami metod klasy, są pomijane.
 */
export interface Restorable {
  readonly name: string;
  readonly prototype: object;
  fromJSON?(data: unknown): unknown;
}

/** Odpowiednik InvalidClassException: zapisane dane są w formacie, którego klasa nie przyjmuje. */
export class InvalidClassError extends Error {
  override name = 'InvalidClassError';
}

export function save(value: object): string {
  return JSON.stringify({ type: value.constructor.name, data: value });
}

export function load(json: string, ...types: Restorable[]): unknown {
  const { type, data } = JSON.parse(json) as { type: string; data: Record<string, unknown> };
  const target = types.find((candidate) => candidate.name === type);
  if (target === undefined) {
    throw new IllegalStateError('nieznana klasa: ' + type);
  }
  if (target.fromJSON !== undefined) {
    return target.fromJSON(data);
  }
  const restored = Object.create(target.prototype) as Record<string, unknown>;
  for (const [key, value] of Object.entries(data)) {
    if (!(key in restored)) {
      restored[key] = value;
    }
  }
  return restored;
}
